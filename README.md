# Keycloak's OTP Form Authentication Extension

To add custom pre-authentication procedure before the original Keycloak's OTP Form,
and custom post-authentication procedure after the original Keycloak's OTP Form,
without make any change to the original Keycloak's OTP Form behaviour

## How To
- build and compile
- put into deployment folder
- open "Authentication"
- select Browser on drop-down and click "Copy"
- input the name of "atrbpn-browser-grant"
- add "ATR BPN OTP Form Extension Pre-Authentication" (REQUIRED) right before "OTP Form"
- add "ATR BPN OTP Form Extension Post-Authentication" (REQUIRED) right after "OTP Form"
