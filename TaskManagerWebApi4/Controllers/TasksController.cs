using Microsoft.AspNetCore.Mvc;
using System.Collections.Generic;
using TaskManagerWebApi4.Models;
using System.Linq;

namespace TaskManagerWebApi4.Controllers
{
    [Route("api/[controller]")]
    public class TasksController : Controller
    {
        private static List<Task> tasks = new List<Task>();
        [HttpGet]
        public IEnumerable<Task> DoGet()
        {
            return tasks;
        }

        [HttpGet("{id}", Name = "GetTask")]
        public IActionResult DoGetById(string id)
        {
            var item = tasks.FirstOrDefault(t => t.ID == id);
            if (item == null) 
                return NotFound();
            
            return new ObjectResult(item);
                    
        }
        
        [HttpPost]
        public IActionResult DoPost([FromBody] Task task)
        {
            if (task == null)
                return BadRequest();
            
            task.ID = (tasks.Count + 1).ToString();
            tasks.Add(task);
            return CreatedAtRoute("GetTask", new {id = task.ID}, task);
        }

        [HttpPut("{id}")]
        public IActionResult DoPut(string id, [FromBody] Task task)
        {
            if (task == null)
                return BadRequest();
            
            var item = tasks.FirstOrDefault(t => t.ID == id);

            if (item == null)
                return NotFound();

            item.Title = task.Title;
            item.Resume = task.Resume;
            item.IsDone = task.IsDone;

            return new ObjectResult(item);
        }

        [HttpDelete("{id}")]
        public IActionResult DoDelete(string id)
        {
            var item = tasks.FirstOrDefault(t => t.ID == id);
            if (item == null) 
                return NotFound();

            tasks.Remove(item)
            return new NoContentResult();
        }
    }
}