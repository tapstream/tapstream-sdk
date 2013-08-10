#include "Tapstream/Tapstream.h"

extern "C" {
	void Config_Set(const char *key, const char *value);
	void Config_Set(const char *key, bool value);
	void Config_Set(const char *key, int value);
	void Config_Set(const char *key, unsigned int value);
	void Config_Set(const char *key, double value);


	void *Event_New(const char *name, bool oneTimeOnly);
	void Event_AddPair(void *event, const char *key, const char *value);
	void Event_AddPair(void *event, const char *key, bool value);
	void Event_AddPair(void *event, const char *key, int value);
	void Event_AddPair(void *event, const char *key, unsigned int value);
	void Event_AddPair(void *event, const char *key, double value);

	void Tapstream_Create(const char *accountName, const char *developerSecret, void *config);
	void Tapstream_FireEvent(void *event);
}