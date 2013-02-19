using System.IO.IsolatedStorage;
using System;
using System.Collections.Generic;
using System.Diagnostics;

//THIS FILE IS PROPERTY CollegeCODE
//AUTHOR: SAURABH JOSHI (battlex94@gmail.com)
//DATA HANDLER FOR THE WHOLE APP



public class AppSettings
{
    private static IsolatedStorageSettings Settings = System.IO.IsolatedStorage.IsolatedStorageSettings.ApplicationSettings;
    
    private List<String> details;

    private String subjectName;
    private String slot;
    private String type;
    private String percentage;
    private String subjectCode;
    private String regDate;
    private String classnbr;
    private int Attended;
    private int subLength;
    private int Conducted;

    public  void StoreSetting(string settingName, string value)
    {
        StoreSetting<string>(settingName, value);
    }

    public void StoreSetting<TValue>(string settingName, TValue value)
    {
        if (!Settings.Contains(settingName))
            Settings.Add(settingName, value);
        else
            Settings[settingName] = value;

        Settings.Save();
    }

    public void saveList(string key , List<String> data)
    {
        int i;
        for (i = 0; i < data.Count; i++) {
            StoreSetting(key + i, data[i]);
        }

        StoreSetting(key + "SIZE", Convert.ToString(data.Count));

    }

    public List<String> loadList(string key)
    {
        List<String> temp = new List<String>();
        String st_int;
        TryGetSetting<String>(key + "SIZE", out st_int);
        int size = Convert.ToInt32(st_int);
        for (int i = 0; i < size; i++) { 
            string t;
            TryGetSetting<string>(key+i,out t);
            temp.Add(t);
        }
        return temp;

    }

    public void loadAttendance(int subnum){
        List<String> data = new List<String>();
        data = loadList("ATTENDANCE");
        int breaker = 0;

        for (int i = 0; i < data.Count; i++)
        {
            if (data[i] == ("BREAK" + subnum))
            {
                breaker = i + 1;
            }
        }
        setSubjectCode(data[breaker]);
        setSubjectName(data[breaker + 1]);
        setType(data[breaker + 2]);
        setSlot(data[breaker + 3]);

        try
        {
            setAttended(Convert.ToInt32(data[breaker + 4]));
            setConducted(Convert.ToInt32(data[breaker + 5]));
            setPercentage(data[breaker + 6]);
        }
        catch (Exception e)
        {
            Debug.WriteLine(e.Message);
            setAttended(0);
            setConducted(0);
            setPercentage("Not Uploaded");
        }
        setRegDate(data[breaker + 7]);
        setClassNbr(data[breaker + 8]);

        List<String> temp = new List<String>();
        
        for (int k = breaker + 9; k < data.Count; k++) {
            if (data[k] == "BREAK" + (subnum + 1)) { break; }
            else temp.Add(data[k]);
        }

        setDetails(temp);

    }

    public bool TryGetSetting<TValue>(string settingName, out TValue value)
    {
        if (Settings.Contains(settingName))
        {
            value = (TValue)Settings[settingName];
            return true;
        }

        value = default(TValue);
        return false;
    }

    public String getSubjectName()
    {
        return subjectName;
    }

    public void setSubjectName(String subjectName)
    {
        this.subjectName = subjectName;
    }

    public String getSlot()
    {
        return slot;
    }

    public void setSlot(String slot)
    {
        this.slot = slot;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getPercentage()
    {
        return percentage;
    }

    public void setPercentage(String percentage)
    {
        this.percentage = percentage;
    }

    public int getAttended()
    {
        return Attended;
    }

    public void setAttended(int attended)
    {
        Attended = attended;
    }

    public int getConducted()
    {
        return Conducted;
    }

    public void setConducted(int conducted)
    {
        Conducted = conducted;
    }

    public String getSubjectCode()
    {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode)
    {
        this.subjectCode = subjectCode;
    }

    public String getRegDate()
    {
        return regDate;
    }

    public void setRegDate(String regDate)
    {
        this.regDate = regDate;
    }
    public String getClassNbr()
    {
        return classnbr;
    }

    public void setClassNbr(String classnbr)
    {
        this.classnbr = classnbr;
    }


    public List<String> getDetails()
    {
        return details;
    }

    public void setDetails(List<String> details)
    {
        this.details = details;
    }

    public int getSubLength()
    {
        return subLength;
    }

    public void setSubLength(int subLength)
    {
        this.subLength = subLength;
    }

}