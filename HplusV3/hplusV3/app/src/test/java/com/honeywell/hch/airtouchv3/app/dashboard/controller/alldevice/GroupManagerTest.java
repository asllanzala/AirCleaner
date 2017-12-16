package com.honeywell.hch.airtouchv3.app.dashboard.controller.alldevice;

import com.honeywell.hch.airtouchv3.HPlusApplication;
import com.honeywell.hch.airtouchv3.framework.config.AppConfig;
import com.honeywell.hch.airtouchv3.framework.webservice.HttpProxy;
import com.honeywell.hch.airtouchv3.framework.webservice.MockWebService;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.net.URLEncoder;

/**
 * Created by Qian Jin on 1/5/16.
 */

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class GroupManagerTest {

    private GroupManager groupManager;
    private MockWebService webService;

    private CreateGroupMethodTest mCreateGroupManager;
    private DeleteGroupMethodTest mDeleteGroupManager;
    private AddDeviceToGroupTest mAddDeviceToGroupManager;
    private DeleteDeviceFromGroupTest mDeleteDeviceFromGroupManager;
    private UpdateGroupNameTest mUpdateGroupNameManager;
    private IsMasterDeviceTest mIsMasterDeviceManager;
    private DeleteDeviceTest mDeleteDeviceManager;
    private SendScenarioToGroupTest mSendScenarioManager;
    private GetGroupByGroupIdTest mGetGroupByGroupIdManager;
    private GroupManagerResponseTest mGroupManagerResponse;

    protected HPlusApplication application = Mockito.mock(HPlusApplication.class, Mockito.RETURNS_DEEP_STUBS);

    @Before
    public void setUp() throws Exception {
        Mockito.when(application.getApplicationContext()).thenReturn(application);
        HPlusApplication.setHPlusApplication(application);

        groupManager = new GroupManager();
        webService = new MockWebService();
        HttpProxy.getInstance().setWebService(webService);
        AppConfig.isTestMode = true;

        mCreateGroupManager = new CreateGroupMethodTest(groupManager, webService);
        mDeleteGroupManager = new DeleteGroupMethodTest(groupManager, webService);
        mAddDeviceToGroupManager = new AddDeviceToGroupTest(groupManager, webService);
        mDeleteDeviceFromGroupManager = new DeleteDeviceFromGroupTest(groupManager, webService);
        mUpdateGroupNameManager = new UpdateGroupNameTest(groupManager, webService);
        mIsMasterDeviceManager = new IsMasterDeviceTest(groupManager, webService);
        mDeleteDeviceManager = new DeleteDeviceTest(groupManager, webService);
        mSendScenarioManager = new SendScenarioToGroupTest(groupManager, webService);
        mGetGroupByGroupIdManager = new GetGroupByGroupIdTest(groupManager, webService);
        mGroupManagerResponse = new GroupManagerResponseTest(groupManager);

    }

    @Test
    public void testCreateGroup() throws Exception {

        mCreateGroupManager.testSuccessCreateGroup();
        mCreateGroupManager.testLoginFailWhenCreateGroup();
        mCreateGroupManager.testFailCreateGroup();
    }


    @Test
    public void testDeleteGroup() throws Exception {
        mDeleteGroupManager.testSuccessDeleteGroup();
        mDeleteGroupManager.testFailDeleteGroup();
        mDeleteGroupManager.testLoginFailWhenDeleteGroup();
    }

    @Test
    public void testAddDeviceToGroup() throws Exception {
        mAddDeviceToGroupManager.testSuccessAddDeviceToGroup();
        mAddDeviceToGroupManager.testFailAddDeviceToGroup();
        mAddDeviceToGroupManager.testLoginFailWhenAddDeviceToGroup();
    }

    @Test
    public void testDeleteDeviceFromGroup() throws Exception {
        mDeleteDeviceFromGroupManager.testSuccessDeleteDeviceFromGroup();
        mDeleteDeviceFromGroupManager.testFailDeleteDeviceFromGroup();
        mDeleteDeviceFromGroupManager.testLoginFailWhenDeleteDeviceFromGroup();
    }

    @Test
    public void testUpdateGroupName() throws Exception {
        mUpdateGroupNameManager.testSuccessUpdateGroupName();
        mUpdateGroupNameManager.testFailUpdateGroupName();
        mUpdateGroupNameManager.testLoginFailWhenUpdateGroupName();
    }

    @Test
    public void testIsMasterDevice() throws Exception {
        mIsMasterDeviceManager.testSuccessIsMasterDevice();
        mIsMasterDeviceManager.testFailIsMasterDevice();
        mIsMasterDeviceManager.testLoginFailWhenIsMasterDevice();
    }

    @Test
    public void testDeleteDevice() throws Exception {
        mDeleteDeviceManager.testSuccessDeleteDevice();
        mDeleteDeviceManager.testFailDeleteDevice();
        mDeleteDeviceManager.testLoginFailWhenDeleteDevice();
    }

    @Test
    public void testSendScenarioToGroup() throws Exception {
        mSendScenarioManager.testSuccessSendScenarioToGroup();
        mSendScenarioManager.testFailSendScenarioToGroup();
        mSendScenarioManager.testLoginFailWhenSendScenarioToGroup();
    }

    @Test
    public void testToURLEncoded() throws Exception {
        String testStr = "";
        Assert.assertEquals("", URLEncodedTest(testStr));
        testStr = null;
        Assert.assertEquals("", URLEncodedTest(testStr));
        testStr = "sljfsl";
        Assert.assertEquals(testStr, URLEncodedTest(testStr));
    }

    @Test
    public void getGroupByGroupId() throws Exception {
        mGetGroupByGroupIdManager.testSuccessGetGroupByGroupId();
        mGetGroupByGroupIdManager.testFailGetGroupByGroupId();
        mGetGroupByGroupIdManager.testLoginFailWhenGetGroupByGroupId();
    }

    private String URLEncodedTest(String paramString) {
        if (paramString == null || paramString.equals("")) {
            return "";
        }
        try {
            String str = new String(paramString.getBytes(), "UTF-8");
            str = URLEncoder.encode(str, "UTF-8");
            return str;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    @Test
    public void testResponse(){
        mGroupManagerResponse.testGroupResponse();
        mGroupManagerResponse.testGetSucceedDevice();
    }


}
