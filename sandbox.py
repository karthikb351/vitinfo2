from BeautifulSoup import BeautifulSoup
import json
keys=["sl_no","code","title","type","slot","regdate","attended","conducted","percentage","extra", "classnbr"]
page=open('C:\Users\karthik\Downloads\\onduty.html')
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
print result
