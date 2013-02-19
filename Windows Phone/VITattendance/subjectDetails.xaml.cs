using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;
using Microsoft.Phone.Controls;

namespace VITattendance
{
    public partial class subjectDetails : PhoneApplicationPage
    {

        int subnum;
        double attended, conducted, t1, t2 , per;
        protected override void OnNavigatedTo(System.Windows.Navigation.NavigationEventArgs e)
        {
            base.OnNavigatedTo(e);
            string st_subnum;
            NavigationContext.QueryString.TryGetValue("selectedItem", out st_subnum);
            subnum = Convert.ToInt32(st_subnum);
            InitializeComponent();
            loadAll();
        }
        public subjectDetails()
        
        {
            
            
        }

        private void loadAll() {
            t1 = 0.0;
            t2 = 0.0;
            AppSettings dat = new AppSettings();
            dat.loadAttendance(subnum);
            
            Marks_Data m = new Marks_Data();
            m.Subject = "Subject Code";
            m.Slot = dat.getSubjectCode();
            details.Items.Add(m);

            m = new Marks_Data();
            m.Subject = "Type";
            m.Slot = dat.getType();
            details.Items.Add(m);

            m = new Marks_Data();
            m.Subject = "Slot";
            m.Slot = dat.getSlot();
            details.Items.Add(m);
            
            m = new Marks_Data();
            m.Subject = "Attended";
            m.Slot = Convert.ToString(dat.getAttended());
            details.Items.Add(m);

            m = new Marks_Data();
            m.Subject = "Conducted";
            m.Slot = Convert.ToString(dat.getConducted());
            details.Items.Add(m);

            m = new Marks_Data();
            m.Subject = "Percentage";
            m.Slot = Convert.ToString(dat.getPercentage());
            details.Items.Add(m);

            List<String> lst = new List<String>();
            lst = dat.getDetails();
            int i = lst.Count-1;
            while (i >= 0) {
                m = new Marks_Data();
                m.Subject = lst[i-1];
                m.Slot = lst[i];
                moreDetais.Items.Add(m);
                i -= 2;
                  
            }
          

            controller.Title = dat.getSubjectName();
            attended = Convert.ToDouble(dat.getAttended());
            conducted = Convert.ToDouble(dat.getConducted());
            lbl_percent.Text = Convert.ToString(Math.Round((attended / conducted) * 100,1)) + "%";

        }


        private void button1_Click(object sender, RoutedEventArgs e)
        {
            if (t1>0){
                t1 -= 1;
                textBlock1.Text = "If you miss " + t1 + " class(s)";
                conducted -= 1;
                per = Math.Round((attended / conducted) * 100, 1);
                lbl_percent.Text = Convert.ToString(per) + "%";
               
            }

        }

        private void button2_Click(object sender, RoutedEventArgs e)
        {
            if (t1 >= 0)
            {
                t1 += 1;
                textBlock1.Text = "If you miss " + t1 + " class(s)";
                conducted += 1;
                per = Math.Round((attended / conducted) * 100, 1);
                lbl_percent.Text = Convert.ToString(per) + "%";

            }
        }

        private void button4_Click(object sender, RoutedEventArgs e)
        {
            if (t2 >= 0)
            {
                t2 += 1;
                textBlock2.Text = "If you attend " + t2 + " class(s)";
                conducted += 1;
                attended += 1;
                per = Math.Round((attended / conducted) * 100, 1);
                lbl_percent.Text = Convert.ToString(per) + "%";

            }

        }

        private void button3_Click(object sender, RoutedEventArgs e)
        {
            if (t2 > 0)
            {
                t2 -= 1;
                textBlock2.Text = "If you attend " + t2 + " class(s)";
                conducted -= 1;
                attended -= 1;
                per = Math.Round((attended / conducted) * 100, 1);
                lbl_percent.Text = Convert.ToString(per) + "%";

            }

        }
    }
}