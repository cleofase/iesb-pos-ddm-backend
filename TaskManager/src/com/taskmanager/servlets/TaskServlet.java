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
		String id;

		response.setContentType("application/json");
		id = getIDFromURI(request.getRequestURI());

		if (id == null) {
			Gson gson = new Gson();
			String json = gson.toJson(tasks);
			response.getWriter().write(json);
		} else {
			Task task = getTask(id);
			if (task != null) {
				Gson gson = new Gson();
				String json = gson.toJson(task);
				response.getWriter().write(json);				
			} else {
				response.sendError(404, "Task not found");
			}
		}
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

		resp.setContentType("application/json");
		id = getIDFromURI(req.getRequestURI());

		if (id != null) {
			Gson gson = new Gson();
			Task task = gson.fromJson(req.getReader(), Task.class);
			task.setId(id);
			if (updateTask(task)) {
				resp.setStatus(HttpServletResponse.SC_OK);
				String json = gson.toJson(tasks);
				resp.getWriter().write(json);
			} else {
				resp.sendError(404, "Task not found");
			}
		} else {
			resp.sendError(404, "Task not found");
		}
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String id;

		resp.setContentType("application/json");
		id = getIDFromURI(req.getRequestURI());

		if (id != null) {
			if (deleteTask(id)) {
				resp.setStatus(HttpServletResponse.SC_OK);

				Gson gson = new Gson();
				String json = gson.toJson(tasks);
				resp.getWriter().write(json);
			} else {
				resp.sendError(404, "Task not found");
			}
		} else {
			resp.sendError(404, "Task not found");
		}
	}
	
	private Task getTask(String id) {
		if (id != null) {
			for (Task task: tasks) {
				if (task.getId().equals(id)) {
					return task;
				}
			}
		}
		return null;
	}

	private boolean updateTask(Task task) {
		Task oldTask;

		if (task != null) {
			oldTask = getTask(task.getId());
			if (oldTask != null) {
				oldTask.setTitle(task.getTitle());
				oldTask.setResume(task.getResume());
				oldTask.setIsCompleted(task.getIsCompleted());
				return true;
			}
		}
		return false;
	}

	private boolean deleteTask(String id) {
		for (Task task : tasks) {
			if (task.getId().equals(id)) {
				tasks.remove(task);
				return true;
			}
		}
		return false;
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
