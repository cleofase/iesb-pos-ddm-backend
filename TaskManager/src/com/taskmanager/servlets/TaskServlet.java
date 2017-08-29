package com.taskmanager.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.taskmanager.domains.Task;

/**
 * Servlet implementation class TaskServlet
 */
@WebServlet("/tasks/*")
public class TaskServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static List<Task> tasks;

	//
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public TaskServlet() {
		super();
		tasks = new ArrayList<Task>();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");
		Gson gson = new Gson();
		String json = gson.toJson(tasks);
		response.getWriter().write(json);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");
		Gson gson = new Gson();
		Task task = gson.fromJson(request.getReader(), Task.class);
		tasks.add(task);

		response.setStatus(HttpServletResponse.SC_CREATED);
		response.getWriter().write(gson.toJson(task));
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String id;
		boolean idHasFound = false;
		id = getIDFromURI(req.getRequestURI());
		
		resp.setContentType("application/json");
		
		
		
		if (id != null) {
			Gson gson = new Gson();
			Task task = gson.fromJson(req.getReader(), Task.class);
			task.setId(id);
			for (Task t: tasks) {
				if (t.getId().equals(id)) {
					//Oba achou!!!
					t.setTitle(task.getTitle());
					t.setResume(task.getResume());
					t.setIsCompleted(task.getIsCompleted());
					resp.setStatus(HttpServletResponse.SC_OK);					

					idHasFound = true;
					break;

				}
			}
		}
		
		if (!idHasFound) {
			resp.sendError(404, "Task not found");
		} else {
			Gson gson = new Gson();
			String json = gson.toJson(tasks);
			resp.getWriter().write(json);			
		}
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String id;
		boolean idHasFound = false;
		id = getIDFromURI(req.getRequestURI());
		
		resp.setContentType("application/json");
		
		if (id != null) {
			for (Task task: tasks) {
				if (task.getId().equals(id)) {
					//Oba achou!!!
					tasks.remove(task);
					resp.setStatus(HttpServletResponse.SC_OK);					

					idHasFound = true;
					break;
				}
			}
		}
		
		if (!idHasFound) {
			resp.sendError(404, "Task not found");
		} else {
			Gson gson = new Gson();
			String json = gson.toJson(tasks);
			resp.getWriter().write(json);			
		}
	}

	private String getIDFromURI(String uri) {
		Pattern idRestPattern = Pattern.compile("/tasks/([0-9]*)");

		Matcher matcher = idRestPattern.matcher(uri);
		if (matcher.find() && matcher.groupCount() > 0) {
			return matcher.group(1).trim();
		}
		return null;
	}

}
