#import "Tapstream.h"

extern "C" {
	void Config_SetString(const char *key, const char *value);
	void Config_SetBool(const char *key, bool value);
	void Config_SetInt(const char *key, int value);
	void Config_SetUInt(const char *key, unsigned int value);
	void Config_SetDouble(const char *key, double value);


	void *Event_New(const char *name, bool oneTimeOnly);
	void Event_Delete();
	void Event_AddPairString(void *event, const char *key, const char *value);
	void Event_AddPairBool(void *event, const char *key, bool value);
	void Event_AddPairInt(void *event, const char *key, int value);
	void Event_AddPairUInt(void *event, const char *key, unsigned int value);
	void Event_AddPairDouble(void *event, const char *key, double value);

	void Tapstream_Create(const char *accountName, const char *developerSecret);
	void Tapstream_FireEvent(void *event);
}