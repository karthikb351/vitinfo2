import _mechanize, logging
import webapp2, cookielib
from _mechanize import Browser
from BeautifulSoup import BeautifulSoup
from google.appengine.ext import db
from cookielib import Cookie
import datetime, json
from google.appengine.api import mail

class MainPage(webapp2.RequestHandler):
    def get(self):
        self.response.out.write('<h1>VITacademics Scraping Engine</h1> <h2>Running On Google App Engine.</h2> \n<h4>Last Update: 14th January 2012 </h4><h4>\n\n(c) 2013 CollegeCODE</h4>')

class DetailsExtractor(webapp2.RequestHandler):
	def get(self, regno, dob, subject):
		regno = regno.upper()
		q = User.all()
		q.filter("regno =", regno)
		q.order("-sestime")
		thevalue = "i didnt get it"
		thecookiename = "ASPSESSIONIDQUFTTQDA"
		x=q[0]
		thevalue = x.cookievalue
		thecookiename = x.cookiename
		captcha = x.captcha
		thetime=x.sestime
		nowtime=datetime.datetime.now()
		if((thetime-nowtime).total_seconds()<30):
			br1 = _mechanize.Browser()
			ck = cookielib.Cookie(version=0, name=thecookiename, value=thevalue, port=None, port_specified=False, domain='academics.vit.ac.in', domain_specified=False, domain_initial_dot=False, path='/', path_specified=True, secure=True, expires=None, discard=True, comment=None, comment_url=None, rest={'HttpOnly': None}, rfc2109=False)
			newcj = cookielib.CookieJar()
			newcj.set_cookie(ck)
			br1.set_cookiejar(newcj)
			br1.set_handle_equiv(True)
			br1.set_handle_redirect(True)
			br1.set_handle_referer(True)
			r=br1.open('https://academics.vit.ac.in/parent/attn_report.asp?sem=FS')
			br1.set_handle_robots(False)
			if(r.geturl()=="https://academics.vit.ac.in/parent/attn_report.asp?sem=FS"):
				page=r.read()
				soup=BeautifulSoup(page)
				l = int(subject)
				br1.select_form(nr=l)
				response2 = br1.submit()
				details = BeautifulSoup(response2)
				last = details("td")
				self.response.write(last[len(last)-5].text)
		

class CaptchaGen(webapp2.RequestHandler):
	def get(self, regno):
		regno=regno.upper()
		br= _mechanize.Browser()
		cj = cookielib.CookieJar()
		br.set_cookiejar(cj)
		br.set_handle_equiv(True)
		br.set_handle_redirect(True)
		br.set_handle_referer(True)
		br.set_handle_robots(False)
		r=br.open('https://academics.vit.ac.in/parent/parent_login.asp')
		html=r.read()
		soup=BeautifulSoup(html)
		img = soup.find('img', id='imgCaptcha')
		image_response = br.open_novisit(img['src'])
		user_key = db.Key.from_path('User', regno, parent=None, namespace=None)
		tempuser = db.get(user_key)
		if(tempuser==None):
			tempuser=User(key_name=regno)
			tempuser.tot_count=0
			tempuser.cap_count=0
			tempuser.mark_count=0
			tempuser.att_count=0
			tempuser.valid=0
		for cook in cj:
			tempuser.cookievalue = cook.value
			tempuser.cookiename = cook.name
		tempuser.sestime=datetime.datetime.now()
		tempuser.put()
		self.response.headers['Content-Type'] = 'image/jpeg'
		self.response.out.write(image_response.read())
		

class CaptchaSub(webapp2.RequestHandler):
	def get(self, regno, dob, captcha):
		regno=regno.upper()
		captcha = captcha.upper()
		thevalue = "i didnt get it"
		thecookiename = "ASPSESSIONIDQUFTTQDA"
		user_key = db.Key.from_path('User', regno, parent=None, namespace=None)
		x = db.get(user_key)
		thevalue=x.cookievalue
		thecookiename=x.cookiename
		thetime=x.sestime
		nowtime=datetime.datetime.now()
		if((thetime-nowtime).total_seconds()<30):
			captcha = captcha.upper()
			br1 = _mechanize.Browser()
			ck = cookielib.Cookie(version=0, name=thecookiename, value=thevalue, port=None, port_specified=False, domain='academics.vit.ac.in', domain_specified=False, domain_initial_dot=False, path='/', path_specified=True, secure=True, expires=None, discard=True, comment=None, comment_url=None, rest={'HttpOnly': None}, rfc2109=False)
			r=br1.open('https://academics.vit.ac.in/parent/parent_login.asp')
			html=r.read()
			newcj = cookielib.CookieJar()
			newcj.set_cookie(ck)
			br1.set_cookiejar(newcj)
			br1.set_handle_equiv(True)
			br1.set_handle_redirect(True)
			br1.set_handle_referer(True)
			br1.set_handle_robots(False)
			br1.select_form('parent_login')
			br1.form['wdregno']=regno
			br1.form['vrfcd']=str(captcha)
			br1.form['wdpswd'] = dob
			response=br1.submit()
			if(response.geturl()=="https://academics.vit.ac.in/parent/home.asp"):
				self.response.write("success")
				for cook in newcj:
					x.cookievalue = cook.value
					x.cookiename = cook.name
				x.sestime=nowtime
				x.dob=dob
				x.valid=1
				x.cap_count=x.cap_count+1
				x.tot_count=x.tot_count+1
				x.put()
			else:
				self.response.write("captchaerror")
		else:
			self.response.write("timedout")


class Marks(webapp2.RequestHandler):
	def get(self, regno, dob):
		regno = regno.upper()
		thevalue = "i didnt get it"
		thecookiename = "ASPSESSIONIDQUFTTQDA"
		user_key = db.Key.from_path('User', regno, parent=None, namespace=None)
		x = db.get(user_key)
		thevalue = x.cookievalue
		thecookiename = x.cookiename
		thetime=x.sestime
		nowtime=datetime.datetime.now()
		if((thetime-nowtime).total_seconds()<90):
			br1 = _mechanize.Browser()
			ck = cookielib.Cookie(version=0, name=thecookiename, value=thevalue, port=None, port_specified=False, domain='academics.vit.ac.in', domain_specified=False, domain_initial_dot=False, path='/', path_specified=True, secure=True, expires=None, discard=True, comment=None, comment_url=None, rest={'HttpOnly': None}, rfc2109=False)
			newcj = cookielib.CookieJar()
			newcj.set_cookie(ck)
			br1.set_cookiejar(newcj)
			br1.set_handle_equiv(True)
			br1.set_handle_redirect(True)
			br1.set_handle_referer(True)
			r=br1.open('https://academics.vit.ac.in/parent/marks.asp?sem=FS')
			br1.set_handle_robots(False)
			if(r.geturl()=="https://academics.vit.ac.in/parent/marks.asp?sem=FS"):
				page=r.read()
				soup=BeautifulSoup(page)
				tr=soup('tr', bgcolor="#EDEADE", height="40")
				finalArray = []
				for cook in newcj:
					x.cookievalue = cook.value
					x.cookiename = cook.name
				x.sestime=nowtime
				x.mark_count=x.mark_count+1
				x.tot_count=x.tot_count+1
				x.put()
				for i in tr:
				    newsoup = BeautifulSoup(str(i))
				    td = newsoup('td')
				    nextsoup = BeautifulSoup(str(td))
				    nextvalues = nextsoup('td')
				    theArray = []
				    for x in nextvalues:
				        theArray.append(str(x.text))
				    finalArray.append(theArray)
		self.response.write(json.dumps(finalArray))
			

				
class AttExtractor(webapp2.RequestHandler):
	def get(self, regno, dob):
		regno = regno.upper()
		thevalue = "i didnt get it"
		thecookiename = "ASPSESSIONIDQUFTTQDA"
		user_key = db.Key.from_path('User', regno, parent=None, namespace=None)
		x = db.get(user_key)
		if(x==None):
			self.response.write("timedout")
		else:
			thevalue = x.cookievalue
			thecookiename = x.cookiename
			thetime=x.sestime
			nowtime=datetime.datetime.now()
			if((thetime-nowtime).total_seconds()<30):
				br1 = _mechanize.Browser()
				ck = cookielib.Cookie(version=0, name=thecookiename, value=thevalue, port=None, port_specified=False, domain='academics.vit.ac.in', domain_specified=False, domain_initial_dot=False, path='/', path_specified=True, secure=True, expires=None, discard=True, comment=None, comment_url=None, rest={'HttpOnly': None}, rfc2109=False)
				newcj = cookielib.CookieJar()
				newcj.set_cookie(ck)
				br1.set_cookiejar(newcj)
				br1.set_handle_equiv(True)
				br1.set_handle_redirect(True)
				br1.set_handle_referer(True)
				r=br1.open('https://academics.vit.ac.in/parent/attn_report.asp?sem=WS')
				br1.set_handle_robots(False)
				if(r.geturl()=="https://academics.vit.ac.in/parent/attn_report.asp?sem=WS"):
					page=r.read()
					self.response.write("valid")
					soup=BeautifulSoup(page)
					tr=soup('td') # taking all the tr tags
					length = len(tr) -3
					self.response.write("<table>")
					for i in range(16, length, 1):
						if tr[i].text == "-":
							self.response.write("<tr>Not Uploaded</tr>")
						elif tr[i].text == "":
							self.response.write("<tr>Not Available</tr>")
						else:
							self.response.write("<tr>" + tr[i].text + "</tr>")
					self.response.write("</table>")
					for cook in newcj:
						x.cookievalue = cook.value
						x.cookiename = cook.name
					x.sestime=nowtime
					x.att_count=x.att_count+1
					x.tot_count=x.tot_count+1
					x.put()
					trn = soup.findAll("input")
					#self.response.write(len(trn))
					tags = soup.findAll(attrs={"name": "classnbr"})
					self.response.write("CLSNBR:")
					for tag in tags:
                                            self.response.write("["+tag['value']+"]")
                                        self.response.write("<br/>")
							
						
				else:
					self.response.write("timedout")
			else:
				self.response.write("timedout")


class JAttendance(webapp2.RequestHandler):
	def get(self, regno, dob):
		regno = regno.upper()
		thevalue = "i didnt get it"
		thecookiename = "ASPSESSIONIDQUFTTQDA"
		user_key = db.Key.from_path('User', regno, parent=None, namespace=None)
		x = db.get(user_key)
		thevalue = x.cookievalue
		thecookiename = x.cookiename
		thetime=x.sestime
		nowtime=datetime.datetime.now()
		if((thetime-nowtime).total_seconds()<30):
			br1 = _mechanize.Browser()
			ck = cookielib.Cookie(version=0, name=thecookiename, value=thevalue, port=None, port_specified=False, domain='academics.vit.ac.in', domain_specified=False, domain_initial_dot=False, path='/', path_specified=True, secure=True, expires=None, discard=True, comment=None, comment_url=None, rest={'HttpOnly': None}, rfc2109=False)
			newcj = cookielib.CookieJar()
			newcj.set_cookie(ck)
			br1.set_cookiejar(newcj)
			br1.set_handle_equiv(True)
			br1.set_handle_redirect(True)
			br1.set_handle_referer(True)
			r=br1.open('https://academics.vit.ac.in/parent/attn_report.asp?sem=WS')
			br1.set_handle_robots(False)
			if(r.geturl()=="https://academics.vit.ac.in/parent/attn_report.asp?sem=WS"):
				page=r.read()
				soup=BeautifulSoup(page)
				tr=soup('td') # taking all the tr tags
				length = len(tr) -3
				attArray = []
				for i in range(17, length, 1):
					if tr[i].text == "-":
						attArray.append(str("Not Uploaded"))
					elif tr[i].text == "":
						attArray.append(str("Not Available"))
					else:
						attArray.append(str(tr[i].text))
				#self.response.write("</table>")
				self.response.write(json.dumps(attArray))
				for cook in newcj:
					x.cookievalue = cook.value
					x.cookiename = cook.name
				x.sestime=nowtime
				x.att_count=x.att_count+1
				x.tot_count=x.tot_count+1
				x.put()
			else:
				self.response.write("timedout")
		else:
			self.response.write("timedout")

class Cleanup(webapp2.RequestHandler):
	def get(self):
		q = User.all()
		msg="<h2>Purged the following bogus users</h2>"
		msg+="<ul>"
		c=0
		for a in q:
			if(a.valid==0):
				db.delete(a.key())
				msg=msg+"<pre><li><h3>USER:\t"
				msg=msg+a.key().name()
				msg=msg+"</h3></pre>"
				c=c+1
		msg=msg+"</ul>"
		if(c==0):
			self.response.write("<h3>Nothing to purge today!</h3>")
		else:	
			self.response.write(msg)
			message = mail.EmailMessage(sender="VITacademics Purger <karthikb351@gmail.com>",subject="The following bogus users have been purged")
			message.to = "Karthik Balakrishnan <karthikb351@gmail.com>"
			message.html=msg
			message.send()

class Viewer(webapp2.RequestHandler):
	def get(self):
		q = User.all()
		q.order('-tot_count')
		self.response.write("<ul>")
		for x in q:
			self.response.write("<pre><li><h3>USER:")
			self.response.write(x.key().name())
			self.response.write("\nDOB:")
			self.response.write(x.dob)
			self.response.write("</h3><h4>Total Number of requests:\t")
			self.response.write(x.tot_count)
			self.response.write("</h4>Captcha Sub Count:\t\t")
			self.response.write(x.cap_count)
			self.response.write("\nAttendance Count:\t\t")
			self.response.write(x.att_count)
			self.response.write("\nMarks Count:\t\t\t")
			self.response.write(x.mark_count)
			self.response.write("</li>")
			self.response.write("\n\n</pre>")
		self.response.write("</ul>")
			
			
		
class User(db.Model):
	valid = db.IntegerProperty()	
	tot_count = db.IntegerProperty()
	cap_count = db.IntegerProperty()
	att_count = db.IntegerProperty()
	mark_count = db.IntegerProperty()
	dob = db.StringProperty()
	cookiename = db.StringProperty()
	cookievalue = db.StringProperty()
	sestime = db.DateTimeProperty(auto_now=True)
		



app = webapp2.WSGIApplication([('/', MainPage),('/purge', Cleanup),('/view', Viewer),('/det/(.*)/(.*)/(.*)', DetailsExtractor ), ('/attj/(.*)/(.*)', JAttendance), ('/captchasub/(.*)/(.*)/(.*)', CaptchaSub), ('/marks/(.*)/(.*)', Marks), ('/captcha/(.*)', CaptchaGen), ('/att/(.*)/(.*)', AttExtractor) ] ,debug=True)
