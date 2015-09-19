#import "TSTapstream.h"


void *Config_New()
{
	TSConfig *conf = [TSConfig configWithDefaults];
	return (BRIDGE_RETAINED void *)conf;
}
void Config_Delete(void *conf)
{
	TSConfig *c = (BRIDGE_TRANSFER TSConfig *)conf;
}

void _Config_Set(void *conf, const char *key, NSObject *value)
{
	TSConfig *c = (BRIDGE_TRANSFER TSConfig *)conf;
	NSString *k = [NSString stringWithUTF8String:key];
	if([c respondsToSelector:NSSelectorFromString(k)])
	{
		[c setValue:value forKey:k];
	}
}
void Config_SetString(void *conf, const char *key, const char *value)
{
	_Config_Set(conf, key, [NSString stringWithUTF8String:value]);
}
void Config_SetBool(void *conf, const char *key, bool value)
{
	_Config_Set(conf, key, [NSNumber numberWithInt:(int)value]);
}
void Config_SetInt(void *conf, const char *key, int value)
{
	_Config_Set(conf, key, [NSNumber numberWithInt:value]);
}
void Config_SetUInt(void *conf, const char *key, unsigned int value)
{
	_Config_Set(conf, key, [NSNumber numberWithUnsignedInt:value]);
}
void Config_SetDouble(void *conf, const char *key, double value)
{
	_Config_Set(conf, key, [NSNumber numberWithDouble:value]);
}


void *Event_New(const char *name, bool oneTimeOnly)
{
	NSString *n = [NSString stringWithUTF8String:name];
	TSEvent *event = [TSEvent eventWithName:n oneTimeOnly:oneTimeOnly];
	return (BRIDGE_RETAINED void *)event;
}
void Event_Delete(void *event)
{
	TSEvent *e = (BRIDGE_TRANSFER TSEvent *)event;
}
void Event_AddPairString(void *event, const char *key, const char *value)
{
	NSString *k = [NSString stringWithUTF8String:key];
	NSString *v = [NSString stringWithUTF8String:value];
	[(BRIDGE_TRANSFER TSEvent *)event addValue:v forKey:k];
}
void Event_AddPairBool(void *event, const char *key, bool value)
{
	NSString *k = [NSString stringWithUTF8String:key];
	[(BRIDGE_TRANSFER TSEvent *)event addBooleanValue:(int)value forKey:k];
}
void Event_AddPairInt(void *event, const char *key, int value)
{
	NSString *k = [NSString stringWithUTF8String:key];
	[(BRIDGE_TRANSFER TSEvent *)event addIntegerValue:value forKey:k];
}
void Event_AddPairUInt(void *event, const char *key, unsigned int value)
{
	NSString *k = [NSString stringWithUTF8String:key];
	[(BRIDGE_TRANSFER TSEvent *)event addUnsignedIntegerValue:value forKey:k];
}
void Event_AddPairDouble(void *event, const char *key, double value)
{
	NSString *k = [NSString stringWithUTF8String:key];
	[(BRIDGE_TRANSFER TSEvent *)event addDoubleValue:value forKey:k];
}

void Tapstream_Create(const char *accountName, const char *developerSecret, void *conf)
{
	TSConfig *c = (BRIDGE_TRANSFER TSConfig *)conf;
	NSString *name = [NSString stringWithUTF8String:accountName];
	NSString *secret = [NSString stringWithUTF8String:developerSecret];
	[TSTapstream createWithAccountName:name developerSecret:secret config:c];
}
void Tapstream_FireEvent(void *event)
{
	TSEvent *e = (BRIDGE_TRANSFER TSEvent *)event;
	[[TSTapstream instance] fireEvent:e];
}

void Tapstream_GetConversionData(const char *callbackClass, const char *callbackMethod)
{
	NSString *gameObjectName = [NSString stringWithUTF8String:callbackClass];
	NSString *methodName = [NSString stringWithUTF8String:callbackMethod];
	[[TSTapstream instance] getConversionData:^(NSData *jsonInfo) {
		if(jsonInfo != nil){
			UnitySendMessage(
				[gameObjectName UTF8String],
				[methodName UTF8String],
				[jsonInfo bytes]
			);
		}
	}];
}
