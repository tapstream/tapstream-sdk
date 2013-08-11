#include "TapstreamObjcInterface.h"

static TSConfig *conf = [TSConfig configWithDefaults];

static NSMutableSet *events = [NSMutableSet setWithCapacity:16];

void _Config_Set(const char *key, NSObject *value)
{
	NSString *key = [NSString stringWithUTF8String:key];
	if([conf respondsToSelector:NSSelectorFromString(key)])
	{
		[conf setValue:value forKey:key];
	}
}

void Config_SetString(const char *key, const char *value)
{
	_Config_Set(key, [NSString stringWithUTF8String:value]);
}
void Config_SetBool(const char *key, bool value)
{
	_Config_Set(key, [NSNumber numberWithInt:(int)value]);
}
void Config_SetInt(const char *key, int value)
{
	_Config_Set(key, [NSNumber numberWithInt:value]);
}
void Config_SetUInt(const char *key, unsigned int value)
{
	_Config_Set(key, [NSNumber numberWithUnsignedInt:value]);
}
void Config_SetDouble(const char *key, double value)
{
	_Config_Set(key, [NSNumber numberWithDouble:value]);
}


void *Event_New(const char *name, bool oneTimeOnly)
{
	NSString *name = [NSString stringWithUTF8String:name];
	Event *event = [Event eventWithName:name oneTimeOnly:oneTimeOnly];
	[events addObject:event];
}
void Event_Delete(void *event)
{
	Event *e = (Event *)event;
	[events removeObject:e]
}
void Event_AddPairString(void *event, const char *key, const char *value)
{
	NSString *key = [NSString stringWithUTF8String:key];
	NSString *value = [NSString stringWithUTF8String:value];
	[(Event *)event addValue:value forKey:key];
}
void Event_AddPairBool(void *event, const char *key, bool value)
{
	NSString *key = [NSString stringWithUTF8String:key];
	[(Event *)event addBooleanValue:(int)value forKey:key];
}
void Event_AddPairInt(void *event, const char *key, int value)
{
	NSString *key = [NSString stringWithUTF8String:key];
	[(Event *)event addIntegerValue:value forKey:key];
}
void Event_AddPairUInt(void *event, const char *key, unsigned int value)
{
	NSString *key = [NSString stringWithUTF8String:key];
	[(Event *)event addUnsignedIntegerValue:value forKey:key];
}
void Event_AddPairDouble(void *event, const char *key, double value)
{
	NSString *key = [NSString stringWithUTF8String:key];
	[(Event *)event addDoubleValue:value forKey:key];
}

void Tapstream_Create(const char *accountName, const char *developerSecret)
{
	NSString *accountName = [NSString stringWithUTF8String:accountName];
	NSString *developerSecret = [NSString stringWithUTF8String:developerSecret];
	[Tapstream createWithAccountName:accountName developerSecret:developerSecret config:conf];
}
void Tapstream_FireEvent(void *event)
{
	Event *e = (Event *)event;
	[[Tapstream shared] fireEvent:e];
	[events removeObject:e]
}





