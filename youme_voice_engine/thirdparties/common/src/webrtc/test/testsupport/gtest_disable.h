﻿/*
 *  Copyright (c) 2012 The WebRTC project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
#ifndef TEST_TESTSUPPORT_INCLUDE_GTEST_DISABLE_H_
#define TEST_TESTSUPPORT_INCLUDE_GTEST_DISABLE_H_

// Helper macros for platform disables. These can be chained. Example use:
// TEST_F(ViEStandardIntegrationTest,
//        DISABLED_ON_LINUX(RunsBaseTestWithoutErrors)) {  // ...
//
// Or, you can disable a whole test class by wrapping all mentions of the test
// class name inside one of these macros.
//
// The platform #defines we are looking at here are set by the build system.
#ifdef WEBRTC_LINUX
#define DISABLED_ON_LINUX(test) DISABLED_##test
#else
#define DISABLED_ON_LINUX(test) test
#endif

#ifdef WEBRTC_MAC
#define DISABLED_ON_MAC(test) DISABLED_##test
#else
#define DISABLED_ON_MAC(test) test
#endif

#ifdef _WIN32
#define DISABLED_ON_WIN(test) DISABLED_##test
#else
#define DISABLED_ON_WIN(test) test
#endif

// Using some extra magic here to be able to chain Android and iOS macros.
// http://stackoverflow.com/questions/8231966/why-do-i-need-double-layer-of-indirection-for-macros
#ifdef WEBRTC_ANDROID
#define DISABLED_ON_ANDROID_HIDDEN(test) DISABLED_##test
#define DISABLED_ON_ANDROID(test) DISABLED_ON_ANDROID_HIDDEN(test)
#else
#define DISABLED_ON_ANDROID_HIDDEN(test) test
#define DISABLED_ON_ANDROID(test) DISABLED_ON_ANDROID_HIDDEN(test)
#endif

#ifdef WEBRTC_IOS
#define DISABLED_ON_IOS_HIDDEN(test) DISABLED_##test
#define DISABLED_ON_IOS(test) DISABLED_ON_IOS_HIDDEN(test)
#else
#define DISABLED_ON_IOS_HIDDEN(test) test
#define DISABLED_ON_IOS(test) DISABLED_ON_IOS_HIDDEN(test)
#endif

#endif  // TEST_TESTSUPPORT_INCLUDE_GTEST_DISABLE_H_
