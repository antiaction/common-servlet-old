package com.antiaction.common.servlet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.antiaction.common.servlet.annotations.DELETE;
import com.antiaction.common.servlet.annotations.GET;
import com.antiaction.common.servlet.annotations.POST;
import com.antiaction.common.servlet.annotations.PUT;
import com.antiaction.common.servlet.annotations.Path;
import com.antiaction.common.servlet.annotations.Produces;

@RunWith(JUnit4.class)
public class TestPathManager {

	@Test
	public void test_pathmanager() {
		PathManager pathManager = new PathManager();

		pathManager.register( TestResource.class, new TestResource() );
	}

	@Path("/CONTEXT/")
	public static class TestResource {

		@GET
		@Path("GET")
		@Produces("get/stuff")
		public void getter() {
		}

		@POST
		@Path("/POST")
		@Produces("post/its")
		public void poster() {
		}

		@PUT
		@Path("PUT/")
		@Produces("put/ty")
		public void putter() {
		}

		@DELETE
		@Path("/DELETE/")
		@Produces("delete/trash")
		public void deleter() {
		}

	}

}
