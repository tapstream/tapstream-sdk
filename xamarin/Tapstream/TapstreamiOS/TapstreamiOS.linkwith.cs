using System;
using MonoTouch.ObjCRuntime;

[assembly: LinkWith (
	"TapstreamiOS.a",
     LinkTarget.ArmV7 | LinkTarget.ArmV7s | LinkTarget.Simulator,
     ForceLoad = true,
     Frameworks = "Foundation UIKit",
     LinkerFlags = "-cxx"
     )]
