import _mechanize, logging
import webapp2, cookielib
from _mechanize import Browser
from BeautifulSoup import BeautifulSoup
from google.appengine.ext import db
from cookielib import Cookie
import datetime, json
from google.appengine.api import mail
from google.appengine.api import urlfetch
import urllib

class MainPage(webapp2.RequestHandler):
    def get(self):
        self.response.out.write('<h1>VITacademics Scraping Engine</h1> <h2>Running On Google App Engine.</h2> \n<h4>Last Update: 13th February 2013 </h4><h4>\n\n(c) 2013 CollegeCODE</h4>')

class Status(webapp2.RequestHandler):
	def get(self):
                sub={}
                sub.update({'version':'11'})
                sub.update({'changelog':'Nothing here yet'})
                sub.update({'msg_no':'2'})
                sub.update({'msg_title':'...and We\'re back!'})
                sub.update({'msg_content':'We apologise for the long absence. We underestimated the load on our servers. We have rebuilt the system to try and handle a larger number of users more efficiently. Since we are fetching all the attendance details in one go, it might take a little while longer to fetch your attendance. This is a limitation on VIT\'s part. Not us :P'})
                self.response.write(json.dumps(sub))
class DetailsExtractor(webapp2.RequestHandler):
	def get(self, regno, dob, subject):
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
                                self.response.write("valid%")
				page=r.read()
				soup=BeautifulSoup(page)
				l = int(subject)
				br1.select_form(nr=l)
				response2 = br1.submit()
				details = BeautifulSoup(response2)
				last = details("td")
				lastArray = []
				for i in range(19, len(last), 2):
					lastArray.append(str(last[i].text))
				lastArrayNew = filter (lambda a: a != "-", lastArray)
				self.response.write(json.dumps(lastArrayNew))
			else:
                            self.response.write("timedout")
		

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
		keys=["sl_no","code","title","type","slot","regdate","attended","conducted","percentage","extra", "classnbr"]
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
                                    self.response.write("valid%")
                                    page=r.read()
                                    soup=BeautifulSoup(page)
                                    trs=soup.findAll('tr', attrs={"bgcolor":"#E6F2FF"})
                                    count=0
                                    k=0
                                    subs=[]
                                    for tr in trs:
                                        sub={}
                                        k=0
                                        if(count==0):
                                            count=0
                                        else:
                                            tds = tr.findAll('input')
                                            rows = tr.findAll('td')
                                            for row in rows:
                                                sub.update({keys[k]:str(row.text)})
                                                k=k+1
                                            classnbrs = tr.findAll('input', attrs={"name":"classnbr"})
                                            if(len(classnbrs)==0):
                                                sub.update({keys[k]:'null'})
                                                k=k+1
                                            else:
                                                for classnbr in classnbrs:
                                                    sub.update({keys[k]:str(classnbr['value'])})
                                                    k=k+1
                                            subs.append(sub)
                                        count=count+1
                                    for cook in newcj:
                                            x.cookievalue = cook.value
                                            x.cookiename = cook.name

                                    cookiehead=x.cookiename+"="+x.cookievalue
                                    head={'Cookie':cookiehead}
                                    x.sestime=nowtime
                                    x.att_count=x.att_count+1
                                    x.tot_count=x.tot_count+1
                                    x.put()
                                    rpcs = []
                                    classnbrs = []
                                    iterator=subs.__iter__()
                                    def create_callback(rpc):
                                        return lambda: handle_result(rpc)
                                    def handle_result(rpc):
                                        result = rpc.get_result()
                                        page=result.content
                                        details=BeautifulSoup(page)
                                        last = details("td")
                                        lastArray = []
                                        for i in range(19, len(last), 2):
                                            lastArray.append(str(last[i].text))
                                        c=0
                                        result=[]
                                        count=0
                                        for i in lastArray:
                                            if(c==2):
                                                c=0
                                                if(i!="-"):
                                                    result[count-1]+="("+i+")"
                                            else:
                                                result.append(i)
                                                c=c+1
                                                count=count+1
                                        sub=iterator.next()
                                        sub.update({'details':result})
                                    for sub in subs:
                                        clsnbr=sub['classnbr']
                                        rpc = urlfetch.create_rpc()
                                        rpc.callback = create_callback(rpc)
                                        post={}
                                        post['semcode']='WINSEM2012-13'
                                        post['classnbr']=clsnbr
                                        post['from_date']='02-Jan-2013'
                                        post['to_date']='08-May-2013'
                                        fpost=urllib.urlencode(post)
                                        urlfetch.make_fetch_call(rpc, "https://academics.vit.ac.in/parent/attn_report_details.asp", method='POST', payload=fpost, headers=head)
                                        rpcs.append(rpc)
                                    for rpc in rpcs:
                                        rpc.wait()
                                    self.response.write(json.dumps(subs))
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
		a=0
		for a in q:
			if(a.valid==0):
				db.delete(a.key())
				msg=msg+"<pre><li><h3>USER:\t"
				msg=msg+a.key().name()
				msg=msg+"</h3></pre>"
				c=c+1
			a=a+1
		msg=msg+"</ul>"
		msg=msg+"<h2>Total unique users: "+str(a-c)+"</h2>"
		if(c==0):
			self.response.write("<h3>Nothing to purge today!</h3>")
		else:	
			self.response.write(msg)
			message = mail.EmailMessage(sender="VITacademicsrel Update<karthikb351@gmail.com>",subject="Today's Stats")
			message.to = "Karthik Balakrishnan <karthikb351@gmail.com>"
			message.html=msg
			message.send()

class Viewer(webapp2.RequestHandler):
	def get(self, pwd):
            if(pwd=="thatthingweshouldntbedoing"):
                    q = User.all()
                    q.order('-tot_count')
                    self.response.write("<ul>")
                    count=0
                    for x in q:
                            if(count==5):
                                break;
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
            else:
                self.response.write("This URL does nothing. Now go away.")
			
			
		
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
		



app = webapp2.WSGIApplication([('/', MainPage),('/purge', Cleanup), ('/status', Status),('/view/(.*)', Viewer),('/det/(.*)/(.*)/(.*)', DetailsExtractor ), ('/attj/(.*)/(.*)', JAttendance), ('/captchasub/(.*)/(.*)/(.*)', CaptchaSub), ('/marks/(.*)/(.*)', Marks), ('/captcha/(.*)', CaptchaGen), ('/att/(.*)/(.*)', AttExtractor) ] ,debug=True)
