using System;
using System.Runtime.InteropServices;

namespace Tapstream
{

	internal class NativeApi
	{
		[DllImport ("__Internal")]
		public static extern IntPtr Config_New();

		[DllImport ("__Internal")]
		public static extern void Config_Delete(IntPtr conf);

		[DllImport ("__Internal")]
		public static extern void Config_SetString(IntPtr conf, string key, string val);

		[DllImport ("__Internal")]
		public static extern void Config_SetBool(IntPtr conf, string key, bool val);

		[DllImport ("__Internal")]
		public static extern void Config_SetInt(IntPtr conf, string key, int val);

		[DllImport ("__Internal")]
		public static extern void Config_SetUInt(IntPtr conf, string key, uint val);

		[DllImport ("__Internal")]
		public static extern void Config_SetDouble(IntPtr conf, string key, double val);

		[DllImport ("__Internal")]
		public static extern IntPtr Event_New(string name, bool oneTimeOnly);

		[DllImport ("__Internal")]
		public static extern void Event_Delete(IntPtr ev);

		[DllImport ("__Internal")]
		public static extern void Event_AddPairString(IntPtr ev, string key, string val);

		[DllImport ("__Internal")]
		public static extern void Event_AddPairBool(IntPtr ev, string key, bool val);

		[DllImport ("__Internal")]
		public static extern void Event_AddPairInt(IntPtr ev, string key, int val);

		[DllImport ("__Internal")]
		public static extern void Event_AddPairUInt(IntPtr ev, string key, uint val);

		[DllImport ("__Internal")]
		public static extern void Event_AddPairDouble(IntPtr ev, string key, double val);

		[DllImport ("__Internal")]
		public static extern void Tapstream_Create(string accountName, string developerSecret, IntPtr conf);

		[DllImport ("__Internal")]
		public static extern void Tapstream_FireEvent(IntPtr ev);
	}

	public class Config
	{
		protected internal IntPtr handle = (IntPtr)0;

		public Config()
		{
			handle = NativeApi.Config_New();
		}

		~Config()
		{
			NativeApi.Config_Delete(handle);
		}

		public void Set(string key, object val)
		{
			Type t = val.GetType();
			if(t == typeof(string))
			{
				NativeApi.Config_SetString(handle, key, (string)val);
			}
			else if(t == typeof(bool))
			{
				NativeApi.Config_SetBool(handle, key, (bool)val);
			}
			else if(t == typeof(int) )
			{
				NativeApi.Config_SetInt(handle, key, (int)val);
			}
			else if(t == typeof(uint))
			{
				NativeApi.Config_SetUInt(handle, key, (uint)val);
			}
			else if(t == typeof(double))
			{
				NativeApi.Config_SetDouble(handle, key, (double)val);
			}
			else
			{
				Console.WriteLine("Tapstream config object cannot accept this type: {0}", t);
			}
		}
	}

	public class Event
	{
		protected internal IntPtr handle = (IntPtr)0;

		public Event(string name, bool oneTimeOnly)
		{
			handle = NativeApi.Event_New(name, oneTimeOnly);
		}

		~Event()
		{
			NativeApi.Event_Delete(handle);
		}

		public void AddPair(string key, object val)
		{
			Type t = val.GetType ();
			if(t == typeof(string))
			{
				NativeApi.Event_AddPairString(handle, key, (string)val);
			}
			else if(t == typeof(bool))
			{
				NativeApi.Event_AddPairBool(handle, key, (bool)val);
			}
			else if(t == typeof(int))
			{
				NativeApi.Event_AddPairInt(handle, key, (int)val);
			}
			else if(t == typeof(uint))
			{
				NativeApi.Event_AddPairUInt(handle, key, (uint)val);
			}
			else if(t == typeof(double))
			{
				NativeApi.Event_AddPairDouble(handle, key, (double)val);
			}
			else
			{
				Console.WriteLine("Tapstream event object cannot accept this type: {0}", t);
			}
		}
	}

	public static class Tapstream
	{
		public static void Create(string accountName, string developerSecret, Config conf)
		{
			NativeApi.Tapstream_Create(accountName, developerSecret, conf.handle);
		}

		public static void FireEvent(Event e)
		{
			NativeApi.Tapstream_FireEvent(e.handle);
		}
	}

}

