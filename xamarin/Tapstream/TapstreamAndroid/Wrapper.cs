using System;
using Android.Runtime;
using Android.Content;
using Android.App;

namespace TapstreamMetrics
{
	public class Config
	{
		private IntPtr cls = IntPtr.Zero;

		protected internal IJavaObject handle = null;

		public Config()
		{
			handle = new Java.Lang.Object(
				JNIEnv.CreateInstance("com/tapstream/sdk/Config", "()V"),
				JniHandleOwnership.TransferGlobalRef);
			cls = JNIEnv.GetObjectClass(handle.Handle);
		}

		public void Set(string key, object val)
		{
			string setter = "set" + char.ToUpper(key[0]) + key.Substring(1);

			Type t = val.GetType();
			if(t == typeof(string))
			{
				IntPtr setterId = JNIEnv.GetMethodID (cls, setter, "(Ljava/lang/String;)V");
				if (setterId != IntPtr.Zero) {
					JNIEnv.CallVoidMethod(handle.Handle, setterId, new JValue(new Java.Lang.String((string)val)));
				} else {
					Console.WriteLine("Tapstream config object had no such setter method: {0}", setter);
				}
			}
			else if(t == typeof(bool))
			{
				IntPtr setterId = JNIEnv.GetMethodID (cls, setter, "(Z)V");
				if (setterId != IntPtr.Zero) {
					JNIEnv.CallVoidMethod(handle.Handle, setterId, new JValue((bool)val));
				} else {
					Console.WriteLine("Tapstream config object had no such setter method: {0}", setter);
				}
			}
			else if(t == typeof(int) )
			{
				IntPtr setterId = JNIEnv.GetMethodID (cls, setter, "(I)V");
				if (setterId != IntPtr.Zero) {
					JNIEnv.CallVoidMethod(handle.Handle, setterId, new JValue((int)val));
				} else {
					Console.WriteLine("Tapstream config object had no such setter method: {0}", setter);
				}
			}
			else if(t == typeof(uint))
			{
				IntPtr setterId = JNIEnv.GetMethodID (cls, setter, "(J)V");
				if (setterId != IntPtr.Zero) {
					JNIEnv.CallVoidMethod(handle.Handle, setterId, new JValue((uint)val));
				} else {
					Console.WriteLine("Tapstream config object had no such setter method: {0}", setter);
				}
			}
			else if(t == typeof(double))
			{
				IntPtr setterId = JNIEnv.GetMethodID (cls, setter, "(D)V");
				if (setterId != IntPtr.Zero) {
					JNIEnv.CallVoidMethod(handle.Handle, setterId, new JValue((double)val));
				} else {
					Console.WriteLine("Tapstream config object had no such setter method: {0}", setter);
				}
			}
			else
			{
				Console.WriteLine("Tapstream config object cannot accept this type: {0}", t);
			}
		}
	}

	public class Event
	{
		private static IntPtr addPairId = IntPtr.Zero;

		protected internal IJavaObject handle = null;

		public Event(string name, bool oneTimeOnly)
		{
			handle = new Java.Lang.Object(
				JNIEnv.CreateInstance("com/tapstream/sdk/Event", "(Ljava/lang/String;Z)V", new JValue(new Java.Lang.String(name)), new JValue(oneTimeOnly)),
				JniHandleOwnership.TransferGlobalRef);
			addPairId = JNIEnv.GetMethodID(JNIEnv.GetObjectClass(handle.Handle), "addPair", "(Ljava/lang/String;Ljava/lang/Object;)V");
		}

		public void AddPair(string key, object val)
		{
			JValue jkey = new JValue(new Java.Lang.String(key));

			Type t = val.GetType ();
			if(t == typeof(string))
			{
				JNIEnv.CallVoidMethod(handle.Handle, addPairId, jkey, new JValue(new Java.Lang.String((string)val)));
			}
			else if(t == typeof(bool))
			{
				JNIEnv.CallVoidMethod(handle.Handle, addPairId, jkey, new JValue(new Java.Lang.Boolean((bool)val)));
			}
			else if(t == typeof(int))
			{
				JNIEnv.CallVoidMethod(handle.Handle, addPairId, jkey, new JValue(new Java.Lang.Integer((int)val)));
			}
			else if(t == typeof(uint))
			{
				JNIEnv.CallVoidMethod(handle.Handle, addPairId, jkey, new JValue(new Java.Lang.Long((uint)val)));
			}
			else if(t == typeof(double))
			{
				JNIEnv.CallVoidMethod(handle.Handle, addPairId, jkey, new JValue(new Java.Lang.Double((double)val)));
			}
			else
			{
				Console.WriteLine("Tapstream event object cannot accept this type: {0}", t);
			}
		}
	}

	public static class Tapstream
	{
		private static IntPtr cls = JNIEnv.FindClass("com/tapstream/sdk/Tapstream");
		private static IntPtr getInstanceId = JNIEnv.GetStaticMethodID(cls, "getInstance", "()Lcom/tapstream/sdk/Tapstream;");
		private static IntPtr fireEventId = JNIEnv.GetMethodID(cls, "fireEvent", "(Lcom/tapstream/sdk/Event;)V");

		public static void Create(Application app, string accountName, string developerSecret, Config conf)
		{
			JNIEnv.CallStaticVoidMethod(cls,
                JNIEnv.GetStaticMethodID(cls, "create", "(Landroid/app/Application;Ljava/lang/String;Ljava/lang/String;Lcom/tapstream/sdk/Config;)V"),
			    new JValue(app), new JValue(new Java.Lang.String(accountName)), new JValue(new Java.Lang.String(developerSecret)), new JValue(conf.handle.Handle));
		}

		public static void FireEvent(Event e)
		{
			IntPtr inst = JNIEnv.CallStaticObjectMethod (cls, getInstanceId);
			JNIEnv.CallVoidMethod(inst, fireEventId, new JValue(e.handle.Handle));
		}
	}
}

