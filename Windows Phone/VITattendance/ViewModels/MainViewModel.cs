using System;
using System.ComponentModel;
using System.Collections.Generic;
using System.Diagnostics;
using System.Text;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;
using System.Collections.ObjectModel;
using System.Net;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;





namespace VITattendance
{
    public class MainViewModel : INotifyPropertyChanged
    {
        public MainViewModel()
        {
            this.Items = new ObservableCollection<ItemViewModel>();
        }
        
        Boolean working = false;
        bool loadedit = false;

        /// <summary>
        /// A collection for ItemViewModel objects.
        /// </summary>
        public ObservableCollection<ItemViewModel> Items { get; private set; }

        private string _sampleProperty = "Sample Runtime Property Value";
        /// <summary>
        /// Sample ViewModel property; this property is used in the view to display its value using a Binding
        /// </summary>
        /// <returns></returns>
        public string SampleProperty
        {
            get
            {
                return _sampleProperty;
            }
            set
            {
                if (value != _sampleProperty)
                {
                    _sampleProperty = value;
                    NotifyPropertyChanged("SampleProperty");
                }
            }
        }

        public bool IsDataLoaded
        {
            get;
            private set;
        }
        
        void RequestComplete(object sender, OpenReadCompletedEventArgs e) {
            working = false;
            if (e.Error == null && !e.Cancelled)
            {
                
            }
            else {
                Deployment.Current.Dispatcher.BeginInvoke(() =>
                {
                    reloadData();
                });
            }
        }

        void DownloadComplete(object sender, DownloadStringCompletedEventArgs e)
        {
            try
            {
                List<String> data = new List<String>();
                AppSettings dat = new AppSettings();
                string jData = e.Result.ToString();
                int count = 0;
                if (jData.Contains("valid%"))
                {
                    jData = jData.Substring(6);
                    JsonTextReader reader = new JsonTextReader(new System.IO.StringReader(jData));
                    JArray root = JArray.Load(reader);
                    foreach (JObject j in root)
                    {
                        data.Add("BREAK" + count); count += 1;
                        data.Add((string)j["code"]); //0
                        data.Add((string)j["title"]); //1
                        data.Add((string)j["type"]); //2
                        data.Add((string)j["slot"]); //3
                        data.Add((string)j["attended"]); //4
                        data.Add((string)j["conducted"]); //5
                        data.Add((string)j["percentage"]); //6
                        data.Add((string)j["regdate"]); //7
                        data.Add((string)j["classnbr"]); //8
                        JArray details = (JArray)j["details"];
                        for (int i = 0; i < details.Count; i++) { data.Add(details[i].ToString()); }
                    }
                    dat.StoreSetting("NUMBEROFSUBJECTS", Convert.ToString(count - 1));
                    dat.saveList("ATTENDANCE", data);
                   

                }

                Deployment.Current.Dispatcher.BeginInvoke(() =>
                {
                    reloadData();
                });

            }
            catch (Exception k)
            {
                Debug.WriteLine(k.Message);
                Deployment.Current.Dispatcher.BeginInvoke(() =>
                {
                    reloadData();
                });
            }
        }

        private void bw_Done(object sender, RunWorkerCompletedEventArgs e) {
            if (loadedit == false)
            {
                 Deployment.Current.Dispatcher.BeginInvoke(() =>
                {
                    reloadData();
                });
            }
            else {
                loadedit = false;
            }
        }

        private void bw_DoWork(object sender, DoWorkEventArgs e) {
            try
            {
            WebClient submit;
            
            working = true;
            
            string url, url2, regno, date, month, year , captcha;
            
            AppSettings dat = new AppSettings();
            dat.TryGetSetting<String>("REGNO", out regno);
            dat.TryGetSetting<String>("DAY", out date);
            dat.TryGetSetting<String>("MONTH", out month);
            dat.TryGetSetting<String>("YEAR", out year);
            dat.TryGetSetting<String>("CAPTCHA", out captcha);
            
            url = "http://vitacademicsdev.appspot.com/captchasub/" + regno + "/" + date + month + year + "/" + captcha ;
            url2 = "http://vitacademicsdev.appspot.com/attj/" + regno + "/" + date + month + year;

            
            
                submit = new WebClient(); 
                submit.OpenReadCompleted += new OpenReadCompletedEventHandler(RequestComplete);
                submit.OpenReadAsync(new Uri(url));

                while (working == true)
                    System.Threading.Thread.Sleep(10);
                

                submit = new WebClient();
                submit.DownloadStringCompleted += new DownloadStringCompletedEventHandler(DownloadComplete);
                submit.DownloadStringAsync(new Uri(url2));
                
            }
            catch (Exception ex)
            {
                Debug.WriteLine(ex); Deployment.Current.Dispatcher.BeginInvoke(() =>
                {
                    reloadData();
                });
            }



        }

        private void reloadData() {
            try
            {
                this.Items.Clear();
                AppSettings dat = new AppSettings();

                string st_num;
                dat.TryGetSetting<string>("NUMBEROFSUBJECTS", out st_num);
                Debug.WriteLine(st_num);
                for (int i = 0; i <= Convert.ToInt32(st_num); i++)
                {
                    dat.loadAttendance(i);
                    this.Items.Add(new ItemViewModel() { LineOne = dat.getSubjectName(), LineTwo = dat.getSlot(), LineThree = dat.getPercentage() });
                }

                var currentPage = ((App)Application.Current).RootFrame.Content as Microsoft.Phone.Controls.PhoneApplicationPage;
                MainPage m = (MainPage)currentPage;
                m.hidePrg();
                m.Controller.Visibility = Visibility.Visible;
                m.textBlock2.Visibility = Visibility.Collapsed;
                m.refresh.Visibility = Visibility.Visible;
                loadedit = true;
            }
            catch (Exception ex) { MessageBox.Show("Error occured while loading attendance"); }
        }

        /// <summary>
        /// Creates and adds a few ItemViewModel objects into the Items collection.
        /// </summary>
        public void LoadData()
        {
            var currentPage = ((App)Application.Current).RootFrame.Content as Microsoft.Phone.Controls.PhoneApplicationPage;
            MainPage m = (MainPage)currentPage;
            m.showPrg();
            //m.Controller.Visibility = Visibility.Collapsed;
            m.refresh.Visibility = Visibility.Collapsed;
            //m.textBlock2.Visibility = Visibility.Visible;
            this.Items.Clear();
            
           String offline;
           this.Items.Add(new ItemViewModel() { LineOne = "Loading..", LineTwo = "", LineThree = "" });

            AppSettings dat = new AppSettings();
            
            dat.TryGetSetting<string>("OFFLINE", out offline);
            
            if (offline == "True")
            {
                Deployment.Current.Dispatcher.BeginInvoke(() =>
                {
                    reloadData();
                });
            }
            else {

                BackgroundWorker bw = new BackgroundWorker();
                bw.DoWork += new DoWorkEventHandler(bw_DoWork);
               
                
                bw.RunWorkerAsync();
            }
            // Sample data; replace with real data
            

            this.IsDataLoaded = true;
        }

        public event PropertyChangedEventHandler PropertyChanged;
        private void NotifyPropertyChanged(String propertyName)
        {
            PropertyChangedEventHandler handler = PropertyChanged;
            if (null != handler)
            {
                handler(this, new PropertyChangedEventArgs(propertyName));
            }
        }
    }
}