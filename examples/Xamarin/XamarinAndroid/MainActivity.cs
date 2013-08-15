using System;
using Android.App;
using Android.Content;
using Android.Runtime;
using Android.Views;
using Android.Widget;
using Android.OS;

using TapstreamMetrics;

namespace XamarinAndroid
{
	[Activity (Label = "XamarinAndroid", MainLauncher = true)]
	public class MainActivity : Activity
	{
		int count = 1;

		protected override void OnCreate (Bundle bundle)
		{
			base.OnCreate (bundle);

			// Set our view from the "main" layout resource
			SetContentView (Resource.Layout.Main);

			// Get our button from the layout resource,
			// and attach an event to it
			Button button = FindViewById<Button> (Resource.Id.myButton);
			
			button.Click += delegate {
				button.Text = string.Format ("{0} clicks!", count++);
			};

			Config conf = new Config();
			conf.Set("fireAutomaticInstallEvent", false);
			conf.Set("openEventName", "xamarin open");
			Tapstream.Create(this, "sdktest", "YGP2pezGTI6ec48uti4o1w", conf);

			Event e = new Event("test-event", false);
			e.AddPair("level", 5);
			e.AddPair("name", "john doe");
			e.AddPair ("score", 10.6);
			Tapstream.FireEvent(e);
		}
	}
}


