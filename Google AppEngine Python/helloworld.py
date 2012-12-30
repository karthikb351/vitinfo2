#updated on 11/10/2012 - "SLot" on "last updated" issue fix.

import _mechanize
import webapp2, cookielib
from _mechanize import Browser
from BeautifulSoup import BeautifulSoup
from google.appengine.ext import db
from cookielib import Cookie
import datetime

class MainPage(webapp2.RequestHandler):
    def get(self):
        self.response.headers['Content-Type'] = 'text/plain'
        self.response.out.write('VITattendance Scraping Base \nRunning On The Google App Engine. \nLast Update: 22 December 2012 \n\n(c) 2012 Siddharth Gupta (B.Tech ECE - VIT)')

class DetailsExtractor(webapp2.RequestHandler):
	def get(self, regno, dob, subject):
		self.response.headers['Content-Type'] = 'text/html'
		br= _mechanize.Browser()
		cj = cookielib.LWPCookieJar()
		br.set_cookiejar(cj)
		br.set_handle_equiv(True)
		br.set_handle_redirect(True)
		br.set_handle_referer(True)
		br.set_handle_robots(False)
		

class CaptchaGen(webapp2.RequestHandler):
	def get(self, regno):
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
		q = User.all()
		q.filter("regno =", regno)
		q.order("-sestime")
		try:
			tempuser=q[0]
		except IndexError:
			tempuser = User()
		tempuser.regno = regno.upper()
		for cook in cj:
			tempuser.cookievalue = cook.value
			tempuser.cookiename = cook.name
		tempuser.sestime=datetime.datetime.now()
		tempuser.put()
		self.response.headers['Content-Type'] = 'image/jpeg'
		self.response.out.write(image_response.read())
		

class CaptchaSub(webapp2.RequestHandler):
	def get(self, regno, dob, captcha):
		q = User.all()
		q.filter("regno =", regno)
		q.order("-sestime")
		thevalue = "i didnt get it"
		thecookiename = "ASPSESSIONIDQUFTTQDA"
		x=q[0]
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
				x.sestime=nowtime
				x.captcha=captcha
				x.put()
			else:
				self.response.write("captchaerror")
		else:
			self.response.write("timedout")
				
class AttExtractor(webapp2.RequestHandler):
	def get(self, regno, dob):
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
				x.sestime=nowtime
				x.put()
			else:
				self.response.write("timedout")
		else:
			self.response.write("timedout")


class Viewer(webapp2.RequestHandler):
     def get(self):
         q = User.all()
         self.response.write("<ul>")
         for x in q:
             self.response.write("<li>")
             self.response.write(x.regno)
             self.response.write(" ---- ")
             self.response.write(x.dob)
             self.response.write("</li>")
         self.response.write("</ul>")
			
			
		
class User(db.Model):
	capcount = db.IntegerProperty()
	attcount = db.IntegerProperty()
	markcount = db.IntegerProperty()
	dob = db.StringProperty()
	regno = db.StringProperty()
	cookiename = db.StringProperty()
	cookievalue = db.StringProperty()
	captcha = db.StringProperty()
	sestime = db.DateTimeProperty(auto_now=True)
		



app = webapp2.WSGIApplication([('/', MainPage),('/view', Viewer), ('/captchasub/(.*)/(.*)/(.*)', CaptchaSub), ('/captcha/(.*)', CaptchaGen), ('/att/(.*)/(.*)', AttExtractor) ],debug=True)