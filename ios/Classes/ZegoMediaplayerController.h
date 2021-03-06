//
//  ZegoMediaplayerController.h
//  Pods-Runner
//
//  Created by 李展鹏@ZEGO on 2020/4/30.
//

#ifndef ZegoMediaplayerController_h
#define ZegoMediaplayerController_h

#import <Foundation/Foundation.h>
#import <Flutter/Flutter.h>
#import <ZegoLiveRoom/zego-api-mediaplayer-oc.h>

@protocol ZegoMediaPlayerControllerDelegate <NSObject>

- (void)onPlayEnd;

- (void)onPlayError:(int)errorCode;

- (void)onBufferBegin;

- (void)onBufferEnd;

- (void)onProcessInterval:(long)timestamp;

@end

@interface ZegoMediaPlayerController : NSObject

+ (instancetype)instance;

//- (void)initPlayerWithType:(MediaPlayerType)type;

//- (void)uninit;

- (void)initObject;

- (void)uninitObject;

- (void)setDelegate:(id<ZegoMediaPlayerControllerDelegate>)delegate;

- (void)start:(NSString *)path repeat:(BOOL)repeat asset:(BOOL)asset reg:(NSObject<FlutterPluginRegistrar>*)reg result:(FlutterResult)result;

- (void)stop:(FlutterResult)result;

- (void)pause:(FlutterResult)result;

- (void)resume:(FlutterResult)result;

- (void)seekTo:(long)millisecond result:(FlutterResult)result;

- (long)getDuration;

- (long)getCurrentDuration;

- (void)muteLocal:(BOOL)mute;

- (void)load:(NSString *)path asset:(BOOL)asset reg:(NSObject<FlutterPluginRegistrar>*)reg result:(FlutterResult)result;

- (void)setVolume:(int)volume;

- (void)setPlayerType:(MediaPlayerType)type;

//- (void)setPublishVolume:(int)volume;

//- (void)setPlayVolume:(int)volume;

//- (int)getPublishVolume;

//- (int)getPlayVolume;

//- (long)setAudioStream:(long)streamIndex;

//- (long)getAudioStreamCount;

- (void)enableRepeatMode:(BOOL)enable;

- (void)setProcessInterval:(long)interval;

@end

#endif /* ZegoMediaplayerController_h */
