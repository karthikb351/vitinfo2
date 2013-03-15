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
using Coding4Fun.Phone.Controls;
using VITattendance.ViewModels;
using System.Windows.Navigation;


namespace VITattendance
{
    public partial class MainPage : PhoneApplicationPage 
    {
        public bool offline = true;
        AppSettings dat;
       
        // Constructor
        public MainPage()
        {

            InitializeComponent();
            // Set the data context of the listbox control to the sample data
            
            DataContext = App.ViewModel;
            this.Loaded += new RoutedEventHandler(MainPage_Loaded);
        }

        public void hidePrg() {
            prg1.Visibility = System.Windows.Visibility.Collapsed;
        }

        public void showPrg()
        {
            prg1.Visibility = System.Windows.Visibility.Visible;
        }

        void messagePrompt_Completed(object sender, PopUpEventArgs<string, PopUpResult> e)
        {
            dat = new AppSettings();
            dat.StoreSetting("OFFLINE", Convert.ToString(offline));
            App.ViewModel.LoadData();
            offline = true;
        }

        private void show_captcha() {
            MessagePrompt messagePrompt = new MessagePrompt();
            messagePrompt.Title = "Enter Captcha";
            messagePrompt.Body = new Captcha();
            messagePrompt.Completed += messagePrompt_Completed;
            messagePrompt.IsCancelVisible = true;
            messagePrompt.Show();
        }


        protected override void OnNavigatedTo(System.Windows.Navigation.NavigationEventArgs e)
        {
            base.OnNavigatedTo(e);
            if (e.NavigationMode == NavigationMode.New)
            {
                IDictionary<string, string> parameters = this.NavigationContext.QueryString;
                if (parameters.ContainsKey("ONLINE"))
                {
                    offline = false;
                }

                dat = new AppSettings();
                String regno;
                bool firstRun;

                firstRun = dat.TryGetSetting<string>("REGNO", out regno);

                //CHECK IF FIRST RUN
                if (firstRun)
                {
                    loadData();

                    //CHECK IF ONLINE OR OFFLINE
                    if (offline) { dat.StoreSetting("OFFLINE", Convert.ToString(offline)); App.ViewModel.LoadData(); }
                    else { offline = false; show_captcha(); }
                }

                else { Controller.DefaultItem = Controller.Items[2]; offline = false; }
            }
            
        }

        private void MainPage_Loaded(object sender, RoutedEventArgs e)
        {
           
            //prg_show = false;
        }

        private void loadData() {
            System.Diagnostics.Debug.WriteLine("Called");
            dat = new AppSettings();
            String regno, day, month, year,st_dat;
            DateTime dater;
            dat.TryGetSetting<string>("REGNO", out regno);
            dat.TryGetSetting<string>("DAY", out day);
            dat.TryGetSetting<string>("MONTH", out month);
            dat.TryGetSetting<string>("YEAR", out year);
            IFormatProvider culture = new System.Globalization.CultureInfo("fr-FR") ;
            txt_REG.Text = regno;
            st_dat = day + "/" + month + "/" + year;
            dater = DateTime.Parse(st_dat, culture);
            datePicker.Value = dater;
        }

        private void button1_Click(object sender, RoutedEventArgs e)
        {
            dat = new AppSettings();

            DateTime dater;
            String regno , day , month , year;
            dater = datePicker.Value.Value;


            if (txt_REG.Text.TrimEnd() != "")
            {
                regno = txt_REG.Text;
                day = checkDate(dater.Day.ToString());
                month = checkDate(dater.Month.ToString());
                year = dater.Year.ToString();

                dat.StoreSetting("REGNO", regno);
                dat.StoreSetting("DAY", day);
                dat.StoreSetting("MONTH", month);
                dat.StoreSetting("YEAR", year);

                offline = false;
                show_captcha();
            }
            else {
                MessageBox.Show("Please enter a valid registration and date of birth", "Error", MessageBoxButton.OK);
            }
        }

        private string checkDate(String dat){
            if (dat.Length == 1)
                dat = "0" + dat;
            return dat;
            }

        private void RoundToggleButton_Checked(object sender, RoutedEventArgs e)
        {
            offline = false;
            show_captcha();
            refresh.IsChecked = false;
        }

        private void PageChanged(object sender, SelectionChangedEventArgs e)
        {
            if (Controller.SelectedIndex == 0)
            {
                refresh.Visibility = System.Windows.Visibility.Visible;
            }
            else
                refresh.Visibility = System.Windows.Visibility.Collapsed;
        }

        private void ListBox_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            if (listBox.SelectedIndex == -1) {
                return;
            }
            
            NavigationService.Navigate(new Uri("/subjectDetails.xaml?selectedItem=" + listBox.SelectedIndex, UriKind.Relative));
        }

        private void txt_REG_TextChanged(object sender, TextChangedEventArgs e)
        {

        }
        
    }
}
