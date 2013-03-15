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
using System.Windows.Media.Imaging;
namespace VITattendance.ViewModels
{
    public partial class Captcha : UserControl
    {
        AppSettings dat = new AppSettings();

        public Captcha()
        {
            InitializeComponent();
            webBrowser1.Visibility = System.Windows.Visibility.Collapsed;
            String regno;
            dat.TryGetSetting<String>("REGNO", out regno);
            webBrowser1.Navigate(new Uri("http://vita-biocross.rhcloud.com/captchaWP.php?regno=" + regno));
            textBox1.Focus();
        }

        private void button1_Click(object sender, RoutedEventArgs e)
        {
            webBrowser1.Visibility = System.Windows.Visibility.Collapsed;
            prg1.Visibility = System.Windows.Visibility.Visible;
            String regno;
            dat.TryGetSetting<String>("REGNO", out regno);
            webBrowser1.Navigate(new Uri("http://vita-biocross.rhcloud.com/captchaWP.php?regno=" + regno)); 
            
        }

        private void webBrowser1_LoadCompleted(object sender, System.Windows.Navigation.NavigationEventArgs e)
        {
            prg1.Visibility = System.Windows.Visibility.Collapsed;
            webBrowser1.Visibility = System.Windows.Visibility.Visible;


        }

        private void webBrowser1_NavigationFailed(object sender, System.Windows.Navigation.NavigationFailedEventArgs e)
        {
            webBrowser1.Visibility = System.Windows.Visibility.Visible;
            prg1.Visibility = System.Windows.Visibility.Collapsed;
            MessageBox.Show("Connection Error");
        }

        private void textBox1_TextChanged(object sender, TextChangedEventArgs e)
        {
            AppSettings dat = new AppSettings();
            dat.StoreSetting("CAPTCHA", textBox1.Text.ToUpper());
        }
    }
}
