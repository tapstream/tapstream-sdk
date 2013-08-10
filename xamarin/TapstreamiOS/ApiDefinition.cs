using System;
using System.Drawing;
using MonoTouch.ObjCRuntime;
using MonoTouch.Foundation;
using MonoTouch.UIKit;

namespace TapstreamiOS
{
	// The first step to creating a binding is to add your native library ("libNativeLibrary.a")
	// to the project by right-clicking (or Control-clicking) the folder containing this source
	// file and clicking "Add files..." and then simply select the native library (or libraries)
	// that you want to bind.
	//
	// When you do that, you'll notice that MonoDevelop generates a code-behind file for each
	// native library which will contain a [LinkWith] attribute. MonoDevelop auto-detects the
	// architectures that the native library supports and fills in that information for you,
	// however, it cannot auto-detect any Frameworks or other system libraries that the
	// native library may depend on, so you'll need to fill in that information yourself.
	//
	// Once you've done that, you're ready to move on to binding the API...
	//
	//
	// Here is where you'd define your API definition for the native Objective-C library.
	//
	// For example, to bind the following Objective-C class:
	//
	//     @interface Widget : NSObject {
	//     }
	//
	// The C# binding would look like this:
	//
	//     [BaseType (typeof (NSObject))]
	//     interface Widget {
	//     }
	//
	// To bind Objective-C properties, such as:
	//
	//     @property (nonatomic, readwrite, assign) CGPoint center;
	//
	// You would add a property definition in the C# interface like so:
	//
	//     [Export ("center")]
	//     PointF Center { get; set; }
	//
	// To bind an Objective-C method, such as:
	//
	//     -(void) doSomething:(NSObject *)object atIndex:(NSInteger)index;
	//
	// You would add a method definition to the C# interface like so:
	//
	//     [Export ("doSomething:atIndex:")]
	//     void DoSomething (NSObject object, int index);
	//
	// Objective-C "constructors" such as:
	//
	//     -(id)initWithElmo:(ElmoMuppet *)elmo;
	//
	// Can be bound as:
	//
	//     [Export ("initWithElmo:")]
	//     IntPtr Constructor (ElmoMuppet elmo);
	//
	// For more information, see http://docs.xamarin.com/ios/advanced_topics/binding_objective-c_libraries
	//

	[BaseType (typeof(NSObject))]
	interface Config
	{

	}

	[BaseType (typeof(NSObject))]
	[DisableDefaultCtor]
	interface Event
	{
		// + (id)eventWithName:(NSString *)name oneTimeOnly:(BOOL)oneTimeOnly;
		[Static, Export ("eventWithName:oneTimeOnly:")]
		Event EventWithName(string name, bool oneTimeOnly);

		// - (void)addValue:(NSString *)value forKey:(NSString *)key;
		[Export ("addValue:forKey:")]
		void AddValue(string value, string key);

		// - (void)addIntegerValue:(int)value forKey:(NSString *)key;
		[Export ("addIntegerValue:forKey:")]
		void AddIntegerValue(int value, string key);

		// - (void)addUnsignedIntegerValue:(uint)value forKey:(NSString *)key;
		[Export ("addUnsignedIntegerValue:forKey:")]
		void AddUnsignedIntegerValue(uint value, string key);

		// - (void)addDoubleValue:(double)value forKey:(NSString *)key;
		[Export ("addDoubleValue:forKey:")]
		void AddDoubleValue(double value, string key);

		// - (void)addBooleanValue:(BOOL)value forKey:(NSString *)key;
		[Export ("addBooleanValue:forKey:")]
		void AddBooleanValue(bool value, string key);
	}

	[BaseType (typeof(NSObject))]
	[DisableDefaultCtor]
	interface Tapstream
	{
		[Static, Export ("createWithAccountName:developerSecret:config:")]
		void Create (string accountName, string developerSecret, Config config);

		// + (id)instance;
		[Static, Export ("instance")]
		Tapstream Instance { get; }

		[Export ("fireEvent:")]
		void FireEvent (Event ev);
	}


}

