from BeautifulSoup import BeautifulSoup
import json
keys=["sl_no","code","title","type","slot","regdate","attended","conducted","percentage","extra", "classnbr"]
page=open('C:\Users\karthik\Downloads\TestAttenIn.htm')
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
print json.dumps(subs)
<form method='POST' action='attn_report_details.asp?semcode=WINSEM2012-13&classnbr=1111&from_date=02-Jan-2013&to_date=08-May-2013'>
<td align='center' valign='center'>
post={}
post['semcode']='WINSEM2012-13'
post['classnbr']=
post['from_date']='02-Jan-2013'
post['to_date']='08-May-2013'
<input type='hidden' name='classnbr' value='4433'>
<input type='hidden' name='from_date' value='02-Jan-2013'>
<input type='hidden' name='to_date' value='08-May-2013'>
<input class='submit' type='submit' value='Details'>
