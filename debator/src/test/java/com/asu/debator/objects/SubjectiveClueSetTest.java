package com.asu.debator.objects;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubjectiveClueSetTest {

	final static Logger log = LoggerFactory.getLogger(SubjectiveClueSetTest.class);
	
	@Test
	public void subjectiveCluesSizeTest(){
		Assert.assertEquals(4746, SubjectiveClueSet.getInstance().getStrongClues().size());
		Assert.assertEquals(2189, SubjectiveClueSet.getInstance().getWeakClues().size()); 
		Assert.assertEquals(8198, SubjectiveClueSet.getInstance().getSubjectiveClues().size());
	}
}
