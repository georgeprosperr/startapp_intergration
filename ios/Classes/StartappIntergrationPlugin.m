#import "StartappIntergrationPlugin.h"
#if __has_include(<startapp_intergration/startapp_intergration-Swift.h>)
#import <startapp_intergration/startapp_intergration-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "startapp_intergration-Swift.h"
#endif

@implementation StartappIntergrationPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftStartappIntergrationPlugin registerWithRegistrar:registrar];
}
@end
