using System;
using ObjCRuntime;

[assembly: LinkWith (
	"TapstreamiOS.a",
     LinkTarget.ArmV7 | LinkTarget.ArmV7s | LinkTarget.Simulator,
     ForceLoad = true,
     Frameworks = "Foundation UIKit",
     IsCxx = true
     )]
