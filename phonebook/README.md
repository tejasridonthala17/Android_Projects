# Phone Book + Custom SMS (single Android app, Kotlin)

## Open & run
1. Android Studio -> File -> Open -> select this `PhoneBook` folder.
2. Let Gradle sync (needs internet the first time). If it asks to upgrade AGP/Gradle, accept.
3. Run on an emulator or a real phone (API 24+).
4. On first launch, tap ALLOW for Contacts and SMS permissions.

## Tabs (bottom navigation, text only - no images)
- Contacts : add / edit / delete, Export & Import JSON (file picker)
- Device   : lists real phone contacts; "Sync" copies them in (phone number = unique key, no duplicates)
- SMS      : reads device inbox, edit / delete, Export & Import SMS as JSON
- Messages : custom messaging. Edit / delete conversations and messages, Export & Import SMS as JSON.
             "+" opens Compose, which loads contacts through the custom PhoneBookProvider via ContentResolver.

## Notes
- Sent messages are stored inside the app only (not sent over the real SMS network).
- Emulator has no SMS/contacts by default: add some in the emulator's Contacts app,
  or use Extended Controls -> Phone to send a test SMS.
