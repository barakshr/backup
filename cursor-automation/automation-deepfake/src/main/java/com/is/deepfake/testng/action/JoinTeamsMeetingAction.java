package com.is.deepfake.testng.action;

import java.lang.reflect.Method;

import com.is.deepfake.product.Meeting;
import com.is.deepfake.testng.annotation.DeepfakeAnnotation;
import com.is.deepfake.testng.context.DeepfakeContextHolder;
import com.is.deepfake.testng.context.DeepfakeTestContext;
import com.is.infra.testng.action.AbstractAction;

public class JoinTeamsMeetingAction extends AbstractAction {

    @Override
    public boolean appliesTo(Method testMethod) {
        DeepfakeAnnotation setup = getAnnotation(testMethod, DeepfakeAnnotation.class);
        return setup != null && setup.joinTeamsMeeting();
    }

    @Override
    public void setup(Method testMethod) {
        Meeting meeting = new Meeting("1234567890");
        DeepfakeContextHolder.set(new DeepfakeTestContext(meeting));
    }
    

    @Override
    public void teardown() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'teardown'");
    }
    
}
