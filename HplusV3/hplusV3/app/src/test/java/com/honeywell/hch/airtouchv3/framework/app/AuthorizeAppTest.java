//package com.honeywell.hch.airtouch.framework.app;
//
//import org.junit.runner.RunWith;
//import org.robolectric.RobolectricTestRunner;
//
///**
// * This is an unit test example class. Need to be refined when developer are writing unit test.
// */
//@RunWith(RobolectricTestRunner.class)
//public class AuthorizeAppTest {
//
////    private IActivityReceive mySuccessLoginReceiver = new IActivityReceive() {
////        @Override
////        public void onReceive(ResponseResult responseResult) {
////            Assert.assertEquals(true, responseResult.isResult());
////        }
////    };
////
////    private IActivityReceive myFalseLoginReceiver = new IActivityReceive() {
////        @Override
////        public void onReceive(ResponseResult responseResult) {
////            Assert.assertEquals(false, responseResult.isResult());
////        }
////    };
//
////    @Test
////    public void testCurrentUserLogin() {
////        AuthorizeApp authorizeApp = new AuthorizeApp();
////
////        // Mock login false
////        authorizeApp.setIsLoginSuccess(false);
////        authorizeApp.currentUserLogin();
////        Assert.assertEquals(true, authorizeApp.mIsAutoLoginOngoing);
////
////        // Mock login true
////        authorizeApp.setIsLoginSuccess(false);
////        boolean isAutoLoginOngoing = authorizeApp.mIsAutoLoginOngoing;
////        authorizeApp.currentUserLogin();
////        Assert.assertEquals(isAutoLoginOngoing, authorizeApp.mIsAutoLoginOngoing);
////
////        authorizeApp.mMobilePhone = 12345678;
////        authorizeApp.mPassword = "sadfasfsa";
////
////        MockWebService webservice = new MockWebService();
////        HttpProxy.getInstance().setWebService(webservice);
////
////        ResponseResult successResult = new ResponseResult();
////        webservice.setResponseResult(successResult);
////        authorizeApp.setLoginReceiver(mySuccessLoginReceiver);
////
////        authorizeApp.currentUserLogin();
////
////        ResponseResult falseResult = new ResponseResult(false);
////        webservice.setResponseResult(falseResult);
////        authorizeApp.setLoginReceiver(myFalseLoginReceiver);
////
////        authorizeApp.currentUserLogin();
////
////    }
//
//
//}
