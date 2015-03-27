using System;
using ObjCRuntime;

[assembly: LinkWith (
	"TapstreamiOS.a",
	LinkTarget.ArmV7 | LinkTarget.ArmV7s | LinkTarget.Simulator | LinkTarget.Simulator64 | LinkTarget.Arm64,
     ForceLoad = true,
     Frameworks = "Foundation UIKit",
     IsCxx = true
     )]
