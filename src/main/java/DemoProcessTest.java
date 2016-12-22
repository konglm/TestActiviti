/*----------------------------------------------------------------
 *  Copyright (C) 2016山东金视野教育科技股份有限公司
 * 版权所有。 
 *
 * 文件名：
 * 文件功能描述：
 *
 * 
 * 创建标识：
 *
 * 修改标识：
 * 修改描述：
 *----------------------------------------------------------------*/

import java.io.FileInputStream;  
import java.util.List;  
import org.activiti.engine.HistoryService;  
import org.activiti.engine.ProcessEngine;  
import org.activiti.engine.ProcessEngineConfiguration;  
import org.activiti.engine.RepositoryService;  
import org.activiti.engine.TaskService;  
import org.activiti.engine.history.HistoricProcessInstance;  
import org.activiti.engine.runtime.ProcessInstance;  
import org.activiti.engine.task.Task;  
  
public class DemoProcessTest {  
    // diagrams实际路径  
    private static String realPath =   
            "E:\\workspace\\TestActiviti\\src\\main\\resources\\diagrams";  
    public static void main(String[] args) throws Exception {  
        // 创建 Activiti流程引擎  
        ProcessEngine processEngine = ProcessEngineConfiguration  
                .createProcessEngineConfigurationFromResource("activiti.cfg.xml")  
                .buildProcessEngine();  
          
        // 取得 Activiti 服务  
        RepositoryService repositoryService = processEngine.getRepositoryService();  
//        RuntimeService runtimeService = processEngine.getRuntimeService();  
  
        // 部署流程定义  
        repositoryService  
                .createDeployment()  
                .addInputStream("MyProcess.bpmn",new FileInputStream(realPath + "\\MyProcess.bpmn"))  
                .addInputStream("MyProcess.png", new FileInputStream(realPath + "\\MyProcess.png"))  
                .deploy();  
          
        // 启动流程实例  
        ProcessInstance instance = processEngine  
                 .getRuntimeService().startProcessInstanceByKey("myProcess");  
        String procId = instance.getId();  
        System.out.println("procId:"+ procId);  
          
        // 获得第一个任务  
        TaskService taskService = processEngine.getTaskService();  
        List<Task> tasks = taskService.createTaskQuery().taskDefinitionKey("firstTask").list();  
        for (Task task : tasks) {  
            System.out.println("Following task is: taskID -" +task.getId()+" taskName -"+ task.getName());  
            // 认领任务  
            taskService.claim(task.getId(), "kermit");  
        }  
           
        // 查看testUser 现在是否能够获取到该任务  
        tasks = taskService.createTaskQuery().taskAssignee("kermit").list();  
        System.out.println("Number of tasks for kermit: "  
                + taskService.createTaskQuery().taskAssignee("kermit").count());  
        for (Task task : tasks) {  
            System.out.println("Task for kermit: " + task.getName());  
            // 完成任务  
            taskService.complete(task.getId());  
        }  
        System.out.println("Number of tasks for kermit: "  
                + taskService.createTaskQuery().taskAssignee("kermit").count());  
          
  
        // 获取并认领第二个任务  
        tasks = taskService.createTaskQuery().taskDefinitionKey("secondTask").list();  
        for (Task task : tasks) {  
            System.out.println("Following task is : taskID -" +task.getId()+" taskName -"+ task.getName());  
            taskService.claim(task.getId(), "fozzie");  
        }  
          
        //完成第二个任务结束结束流程  
        for (Task task : tasks) {  
            taskService.complete(task.getId());  
        }  
          
        // 核实流程是否结束  
        HistoryService historyService = processEngine.getHistoryService();  
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(procId).singleResult();  
        System.out.println("Process instance end time: " + historicProcessInstance.getEndTime());  
        //在Package Explorer里面可以生产部署bar档，生成bar档后到浏览器端去“部署包”
    }  
} 
