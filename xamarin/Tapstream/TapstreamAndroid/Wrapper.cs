using System;
using Android.Runtime;
using Android.Content;

namespace Tapstream
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
			cls = JNIEnv.GetObjectClass (handle.Handle);
		}

		public void Set(string key, object val)
		{
			Type t = val.GetType();
			if(t == typeof(string))
			{
				IntPtr field = JNIEnv.GetFieldID(cls, key, "java/lang/String");
				if (field != IntPtr.Zero) {
					JNIEnv.SetField(handle.Handle, field, JNIEnv.NewString ((string)val));
				}
			}
			else if(t == typeof(bool))
			{
				IntPtr field = JNIEnv.GetFieldID(cls, key, "java/lang/Boolean");
				if (field != IntPtr.Zero) {
					JNIEnv.SetField (handle.Handle, field, new Java.Lang.Boolean((bool)val).Handle);
				}
			}
			else if(t == typeof(int) )
			{
				IntPtr field = JNIEnv.GetFieldID(cls, key, "java/lang/Integer");
				if (field != IntPtr.Zero) {
					JNIEnv.SetField (handle.Handle, field, new Java.Lang.Integer((int)val).Handle);
				}
			}
			else if(t == typeof(uint))
			{
				IntPtr field = JNIEnv.GetFieldID(cls, key, "java/lang/Long");
				if (field != IntPtr.Zero) {
					JNIEnv.SetField (handle.Handle, field, new Java.Lang.Long((uint)val).Handle);
				}
			}
			else if(t == typeof(double))
			{
				IntPtr field = JNIEnv.GetFieldID(cls, key, "java/lang/Double");
				if (field != IntPtr.Zero) {
					JNIEnv.SetField (handle.Handle, field, new Java.Lang.Double((double)val).Handle);
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
		private static IntPtr addPairId = JNIEnv.GetMethodID (cls, "addPair", "(Ljava/lang/String;Ljava/lang/Object;)V");
		private static IntPtr cls = IntPtr.Zero;

		protected internal IJavaObject handle = null;

		public Event(string name, bool oneTimeOnly)
		{
			handle = new Java.Lang.Object(
				JNIEnv.CreateInstance("com/tapstream/sdk/Event", "(Ljava/lang/String;Z)V", new JValue(new Java.Lang.String(name)), new JValue(oneTimeOnly)),
				JniHandleOwnership.TransferGlobalRef);
			cls = JNIEnv.GetObjectClass (handle.Handle);
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
				JNIEnv.CallVoidMethod(handle.Handle, addPairId, jkey, new JValue((bool)val));
			}
			else if(t == typeof(int))
			{
				JNIEnv.CallVoidMethod(handle.Handle, addPairId, jkey, new JValue((int)val));
			}
			else if(t == typeof(uint))
			{
				JNIEnv.CallVoidMethod(handle.Handle, addPairId, jkey, new JValue((uint)val));
			}
			else if(t == typeof(double))
			{
				JNIEnv.CallVoidMethod(handle.Handle, addPairId, jkey, new JValue((double)val));
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
		private static IntPtr fireEventId = JNIEnv.GetStaticMethodID(cls, "fireEvent", "(Lcom/tapstream/sdk/Event;)V");

		public static void Create(Context context, string accountName, string developerSecret, Config conf)
		{
			JNIEnv.CallStaticVoidMethod(cls,
                JNIEnv.GetStaticMethodID(cls, "create", "(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;Lcom/tapstream/sdk/Config;)V"),
			    new JValue(context), new JValue(new Java.Lang.String(accountName)), new JValue(new Java.Lang.String(developerSecret)), new JValue(conf.handle.Handle));
		}

		public static void FireEvent(Event e)
		{
			JNIEnv.CallStaticVoidMethod(cls, fireEventId, new JValue(e.handle.Handle));
		}
	}
}

