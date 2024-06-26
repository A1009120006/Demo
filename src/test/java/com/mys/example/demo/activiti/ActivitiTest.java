package com.mys.example.demo.activiti;

import com.alibaba.fastjson.JSON;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.*;
import org.activiti.engine.history.*;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 1.      Deployment：流程部署对象，部署一个流程时创建。
 * 2.      ProcessDefinitions：流程定义，部署成功后自动创建。
 * 3.      ProcessInstances：流程实例，启动流程时创建。
 * 4.      Task：任务，在Activiti中的Task仅指有角色参与的任务，即定义中的UserTask。
 * 5.      Execution：执行计划，流程实例和流程执行中的所有节点都是Execution，如UserTask、ServiceTask等。
 *
 * 1.      ProcessEngine：流程引擎的抽象，通过它我们可以获得我们需要的一切服务。
 * 2.      RepositoryService：Activiti中每一个不同版本的业务流程的定义都需要使用一些定义文件，部署文件和支持数据(例如BPMN2.0 XML文件，表单定义文件，流程定义图像文件等)，这些文件都存储在Activiti内建的Repository中。RepositoryService提供了对 repository的存取服务。
 * 3.      RuntimeService：在Activiti中，每当一个流程定义被启动一次之后，都会生成一个相应的流程对象实例。RuntimeService提供了启动流程、查询流程实例、设置获取流程实例变量等功能。此外它还提供了对流程部署，流程定义和流程实例的存取服务。
 * 4.      TaskService: 在Activiti中业务流程定义中的每一个执行节点被称为一个Task，对流程中的数据存取，状态变更等操作均需要在Task中完成。TaskService提供了对用户Task 和Form相关的操作。它提供了运行时任务查询、领取、完成、删除以及变量设置等功能。
 * 5.      IdentityService: Activiti中内置了用户以及组管理的功能，必须使用这些用户和组的信息才能获取到相应的Task。IdentityService提供了对Activiti 系统中的用户和组的管理功能。
 * 6.      ManagementService: ManagementService提供了对Activiti流程引擎的管理和维护功能，这些功能不在工作流驱动的应用程序中使用，主要用于Activiti系统的日常维护。
 * 7.      HistoryService: HistoryService用于获取正在运行或已经完成的流程实例的信息，与RuntimeService中获取的流程信息不同，历史信息包含已经持久化存储的永久信息，并已经被针对查询优化。
 * 现在至少要知道有这些对象和接口。并结合Activiti Api这一章节来看，你就会对部署流程、启动流程、执行任务等操作有一个基本的概念。之后编写一个简单的单元测试，主要为了测试activiti.cfg.xml配置的是否正确，流程是否可以被部署即可。
 * 至于与Spring的集成，一定要熟悉基于Spring配置Activiti，以及事务的处理。
 */
public class ActivitiTest {

    private static ProcessEngine processEngine = null;
    private static ManagementService managementService = null;
    private static RepositoryService repositoryService = null;
    private static DynamicBpmnService dynamicBpmnService = null;
    private static HistoryService historyService = null;
    private static ProcessEngineConfiguration processEngineConfiguration = null;
    private static RuntimeService runtimeService = null;
    private static TaskService taskService = null;
    @BeforeAll
    public static void setup(){
        //ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        processEngine = ProcessEngines.getDefaultProcessEngine(); //流程引擎
        managementService = processEngine.getManagementService();//流程引擎的管理和维护功能
        runtimeService = processEngine.getRuntimeService();//执行管理，包括启动、推进、删除流程实例等操作
        taskService = processEngine.getTaskService();//任务管理
        repositoryService = processEngine.getRepositoryService();//管理流程定义
        dynamicBpmnService = processEngine.getDynamicBpmnService();
        historyService = processEngine.getHistoryService();//历史管理(执行完的数据的管理)
        processEngineConfiguration = processEngine.getProcessEngineConfiguration(); //流程引擎配置信息
    }

    /**
     * 查询已完成/未完成的流程
     */
    @Test
    void qryProcess(){
        /**
         * 查询未完成的流程
         */
        List<ProcessInstance> processInstanceList = runtimeService.createProcessInstanceQuery() // 流程实例查询
//                .processInstanceBusinessKey("")//关联的业务表主键ID
//                .processInstanceId(processInstance.getId())//根据流程实例id获取单个
//                .processInstanceName("demoProcessInstanceName")//流程实例的名称，可能返回多个
                .involvedUser("liuchuxing")
                .orderByProcessInstanceId()
                .desc()
                .list(); // 唯一结果
        for (ProcessInstance item : processInstanceList) {
            System.out.println("  ====== 未完成流程 id=" + item.getId() + ", processInstansId=" + item.getProcessInstanceId() + ", name=" + item.getName() + ", startUserId=" + item.getStartUserId());
        }

        /**
         * 查询已完成的流程
         */
        List<HistoricProcessInstance> historicProcessInstances = historyService.createHistoricProcessInstanceQuery()
                .involvedUser("liuchuxing")
                .orderByProcessInstanceEndTime().asc()
                .list();

        for (HistoricProcessInstance historicProcessInstance : historicProcessInstances) {
            System.out.println("  ====== 已完成流程实例ID: " + historicProcessInstance.getId() + ", 结束时间: " + historicProcessInstance.getEndTime() + "信息 = " + JSON.toJSONString(historicProcessInstance));
        }
        Assertions.assertTrue(true);
    }

    /**
     * 部署流程，并新建一个流程
     */
    @Test
    void deployAndStartProcess(){
        // 部署流程
        repositoryService.createDeployment()
                .addClasspathResource("bpmn/demo.bpmn20.xml")
                .key("demoDefineId")
                .name("测试流程")  // 部署规则的别名
                .deploy();
        /**
         * 新建流程
         */
        Map<String, Object> vars = new HashMap<>();
        vars.put("userIds", "liuchuxing");
        //key 流程定义的
        //多次对同一个流程变量赋值时，流程变量的值不会更新，即后面的赋值永远是不生效的
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("demoDefineId", vars);
        // 设置变量
        runtimeService.setVariable(processInstance.getId(),"var1","value1");
        runtimeService.setProcessInstanceName(processInstance.getId(),"demoProcessInstanceName");
        System.out.println("  ====== 新建流程：" + processInstance.getId() + "," + processInstance.getProcessInstanceId() + "," + processInstance.getName() );
        Assertions.assertTrue(true);
    }

    /**
     * 任务处理：完成这一节点任务。当所有任务处理完毕，对应当前流程实例信息删除，但是可以在历史中查看到该信息
     */
    @Test
    void completeTask(){

        // 查询待办流程
        // 分页：List<Task> list = taskService.createTaskQuery().taskAssignee("小红").processDefinitionKey("test").listPage(i,j);
        List<Task> list = taskService.createTaskQuery() //查询任务
                .taskAssignee("liuchuxing") // 查询待办个人任务
//                .taskCandidateOrAssigned("lisi")  // 查询所有待办任务（个人、组任务）
//                .taskCandidateUser("小红")// 查询待办组任务
//                .processDefinitionKey("demoDefineId") //流程定义ID
                .list();

        for (Task task : list) {
            System.out.println("====== 待办流程 名称：" + task.getName() + "," + "执行人：" + task.getAssignee() + "," + "流程ID：" + task.getId() + "," + "实例ID：" + task.getProcessInstanceId());
            taskService.complete(task.getId());//审批此流程任务节点
        }
        Assertions.assertTrue(true);
    }

    @Test //查看流程定义 （ProcessDefinition）
    public void queryProcessDefinition() throws Exception {//获取仓库服务对象，使用版本的升序排列，查询列表

        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        List<org.activiti.engine.repository.ProcessDefinition> processDefinitionList = processEngine.getRepositoryService().createProcessDefinitionQuery()
                //添加查询条件
//                .processDefinitionId("")
//                .processDefinitionName("提交请假流程")
//                .processDefinitionKey("test-flow")
                //排序
                .orderByProcessDefinitionVersion().asc()
                //查询的结果集
                .list();

        // 遍历集合，查看内容
        for (ProcessDefinition pd : processDefinitionList) {
            System.out.println("id:"+pd.getId());
            System.out.println("name:"+pd.getName());
            System.out.println("key:"+pd.getKey());
            System.out.println("version:"+pd.getVersion());
            System.out.println("resourceName"+pd.getResourceName());
            System.out.println("------------------------------");
        }
    }
    @Test //删除流程定义
    void deleteDeployment(){
        String id = "a1063f67-0b59-11ee-9a36-38d57a012850";
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        repositoryService.deleteDeployment(id);
    }

    @Test //查询流程实例
    public void queryProcess() {
        String processInstanceId = "90d7a464-0c12-11ee-81f2-38d57a012850";
        //获得流程执行服务类对象
        ProcessInstance pi = processEngine.getRuntimeService()
                .createProcessInstanceQuery() // 流程实例查询
                .processInstanceId(processInstanceId)
                .singleResult(); // 唯一结果

        if (pi == null) {
            System.out.println("流程已结束！");
        } else {
            System.out.println("流程定义key:" + pi.getProcessDefinitionKey());
            System.out.println("流程定义名称:" + pi.getProcessDefinitionName());
            System.out.println("name:" + pi.getName());
            System.out.println("当前流程在" + pi.getActivityId());
            System.out.println("业务id：" + pi.getBusinessKey());
            System.out.println("DeploymentId:" + pi.getDeploymentId());
        }
    }
    /**
     * 流程挂起
     */
    @Test
    public void suspend(){
        String processInstanceId = "90d7a464-0c12-11ee-81f2-38d57a012850";
        processEngine.getRuntimeService().suspendProcessInstanceById(processInstanceId);
        System.out.println("暂停/挂起成功");
    }

    /**
     * 流程激活
     */
    @Test
    public void active(){
        String processInstanceId = "90d7a464-0c12-11ee-81f2-38d57a012850";
        processEngine.getRuntimeService().activateProcessInstanceById(processInstanceId);
        System.out.println("激活成功");
    }
    /**
     * 查询正在执行的任务办理人表
     */
    @Test
    public void findRunPersonTask() {
        //任务ID
        String taskId = "5805";
        List<IdentityLink> list = processEngine.getTaskService()
                .getIdentityLinksForTask(taskId);
        if (list != null && list.size() > 0) {
            for (IdentityLink identityLink : list) {
                System.out.println(identityLink.getTaskId() + "   " + identityLink.getType() + "  " + identityLink.getUserId());
                System.out.println("---------------------IdentityLink-----------------------");
                System.out.println("ProcessInstanceId:"+identityLink.getProcessInstanceId() + "   taskId:"+identityLink.getTaskId() );
                System.out.println( "identityLinkType:" + identityLink.getType() + "   UserId:" + identityLink.getUserId());
            }
        }
    }
    /**
     * 流程转办
     */
    @Test
    public void transferAssignee() {
        //获取一个TaskService对象
        TaskService taskService = processEngine.getTaskService();
        //任务转办
        taskService.setAssignee("1ae429da-0be9-11ee-9e49-38d57a012850","xiaoli");

    }
    /**
     * 结束任务
     * @param taskId    当前任务ID
     */
    public void endTask(String taskId) {

        TaskService taskService = processEngine.getTaskService();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //  当前任务
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        BpmnModel bpmnModel = processEngine.getRepositoryService().getBpmnModel(task.getProcessDefinitionId());
        List<EndEvent> endEventList = bpmnModel.getMainProcess().findFlowElementsOfType(EndEvent.class);
        FlowNode endFlowNode = endEventList.get(0);
        FlowNode currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(task.getTaskDefinitionKey());

        //  临时保存当前活动的原始方向
        List originalSequenceFlowList = new ArrayList<>();
        originalSequenceFlowList.addAll(currentFlowNode.getOutgoingFlows());
        //  清理活动方向
        currentFlowNode.getOutgoingFlows().clear();

        //  建立新方向
        SequenceFlow newSequenceFlow = new SequenceFlow();
        newSequenceFlow.setId("newSequenceFlowId");
        newSequenceFlow.setSourceFlowElement(currentFlowNode);
        newSequenceFlow.setTargetFlowElement(endFlowNode);
        List newSequenceFlowList = new ArrayList<>();
        newSequenceFlowList.add(newSequenceFlow);
        //  当前节点指向新的方向
        currentFlowNode.setOutgoingFlows(newSequenceFlowList);

        //  完成当前任务
        taskService.complete(task.getId());

        //  可以不用恢复原始方向，不影响其它的流程
//        currentFlowNode.setOutgoingFlows(originalSequenceFlowList);
    }
    /**
     * 查询历史流程实例  HistoricProcessInstance
     */
    @Test
    public void findHistoryProcessInstance(){
//        String processInstanceId="8769f060-0be4-11ee-a0d2-38d57a012850";
        List<HistoricProcessInstance> list = processEngine.getHistoryService()      //与历史数据（历史表）相关的Service
                .createHistoricProcessInstanceQuery()  //创建历史流程实例查询
//                .processInstanceId(processInstanceId)  //使用流程实例ID查询
                .orderByProcessInstanceStartTime().asc()
                .list();
        for (HistoricProcessInstance instance: list) {
            System.out.println("---------------- HistoricProcessInstance -------------------");
            System.out.println("instanceId:"+instance.getId());
            System.out.println("ProcessDefinitionId:"+instance.getProcessDefinitionId());
            System.out.println("StartTime:"+instance.getStartTime());
            System.out.println("EndTime:"+instance.getEndTime());
            System.out.println("DurationInMillis:"+instance.getDurationInMillis());
        }

    }
    /**
     * 查看历史活动(包括开始节点和结束节点)
     */
    @Test
    void getHistory() {
        //获取HistoryService接口
        HistoryService historyService = processEngine.getHistoryService();
        //获取历史任务列表
        List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId("eb0ec508-0b65-11ee-9f99-38d57a012850") //获取指定流程实例的任务
                .orderByHistoricActivityInstanceStartTime().desc() // 按开始时间排序
                .list();
        for (HistoricActivityInstance ai : list) {
            System.out.println("任务节点ID："+ai.getActivityId());
            System.out.println("任务节点名称："+ai.getActivityName());
            System.out.println("流程定义ID信息："+ai.getProcessDefinitionId());
            System.out.println("流程实例ID信息："+ai.getProcessInstanceId());
            System.out.println("==============================");
        }
    }
    // 根据发布编号查询已经完成的历史任务记录
    @Test
    void queryHisByDeployId() {
        String deployId ="5b0a5c4c-0b62-11ee-91ec-38d57a012850";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        HistoryService hisServ = engine.getHistoryService(); // 历史信息服务
        List<HistoricProcessInstance> instanceList=hisServ.createHistoricProcessInstanceQuery() // 历史流程实例查询
                .deploymentId(deployId)
                .list();
        HistoricTaskInstanceQuery hisTaskInstanceQuery = hisServ.createHistoricTaskInstanceQuery(); // 历史任务实例查询
        for(HistoricProcessInstance processInstance:instanceList){
            System.out.println("流程实例id："+processInstance.getId());
            List<HistoricTaskInstance> theTaskList = hisTaskInstanceQuery
                    .processInstanceId(processInstance.getId())
                    .orderByTaskCreateTime().asc()
                    .list();
            for (HistoricTaskInstance taskInstance : theTaskList) {
                if(taskInstance.getEndTime()!=null){
                    System.out.println("任务id:" + taskInstance.getId());
                    System.out.println("办理人:" + taskInstance.getAssignee());
                    System.out.println("任务名称:" + taskInstance.getName());
                    System.out.println("开始时间:" + sdf.format(taskInstance.getStartTime()) + ",结束时间:"
                            + sdf.format(taskInstance.getEndTime()) + ",耗时(秒钟):" + taskInstance.getDurationInMillis() / 1000);
                }
            }
            System.out.println("=========华丽的分割线====================");
        }
    }
    /**
     * 查询历史流程变量 HistoricVariableInstance
     */
    @Test
    void findHistoryProcessVariables(){
        String processInstanceId = "8769f060-0be4-11ee-a0d2-38d57a012850";
        List<HistoricVariableInstance> list = processEngine.getHistoryService()
                .createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId)
                .list();
        if(list!=null && list.size()>0){
            for (HistoricVariableInstance hti : list) {
                System.out.println("------------------------- HistoricVariableInstance ---------------------------");
                System.out.println(hti.getId()+" ProcessInstanceId:"+hti.getProcessInstanceId()+" taskId:"+hti.getTaskId());
                System.out.println(hti.getVariableName()+": "+ hti.getValue()+" type:"+hti.getVariableTypeName());
            }
        }
    }
    /**
     * 查询历史任务的办理人表
     */
    @Test
    void findHistoryPersonTask() {
        //流程实例ID
        String processInstanceId = "8769f060-0be4-11ee-a0d2-38d57a012850";
        List<HistoricIdentityLink> list = processEngine.getHistoryService()
                .getHistoricIdentityLinksForProcessInstance(processInstanceId);
        if (list != null && list.size() > 0) {
            for (HistoricIdentityLink identityLink : list) {
                System.out.println("---------------------HistoricIdentityLink-----------------------");
                System.out.println("ProcessInstanceId:"+identityLink.getProcessInstanceId() + "   taskId:"+identityLink.getTaskId() );
                System.out.println( "identityLinkType:" + identityLink.getType() + "   UserId:" + identityLink.getUserId());
            }
        }

    }
    /**
     * 设置和获取流程变量
     */
    @Test
    void processVariable(){
        String processInstanceId ="90d7a464-0c12-11ee-81f2-38d57a012850";
        RuntimeService runtimeService = processEngine.getRuntimeService();
        // 设置变量
        runtimeService.setVariable(processInstanceId,"action","请假申请");
        // 获取变量
        System.out.println(runtimeService.getVariable(processInstanceId,"action"));
    }
    /**
     * 设置任务节点变量
     */
    @Test
    void setTaskVariable(){
        String processInstanceId ="90d7a464-0c12-11ee-81f2-38d57a012850";
        TaskService taskService = processEngine.getTaskService();
        List<Task> list = taskService.createTaskQuery().processInstanceId(processInstanceId)
                .list();
        for (Task task : list) {
            System.out.println("任务名称：" + task.getName());
            System.out.println("任务执行人：" + task.getAssignee());
            System.out.println("任务ID：" + task.getId());
            Map<String, Object> variables=new HashMap<String, Object>();
            variables.put("msg", "请假理由4");
            variables.put("user1", "王二4");
            taskService.setVariables(task.getId(),variables);
            taskService.setVariable(task.getId(),"var","setVariable4");
            taskService.setVariableLocal(task.getId(),"var2","VariableLocal4");
            System.out.println(taskService.getVariablesLocal(task.getId()));
            //多次对同一个流程变量赋值时，流程变量的值不会更新，即后面的赋值永远是不生效的
//            taskService.complete(task.getId(),variables);
        }
    }

    /**
     * 获取任务节点变量
     */
    @Test
    void getTaskVariable(){
        String processInstanceId ="90d7a464-0c12-11ee-81f2-38d57a012850";
        TaskService taskService = processEngine.getTaskService();
        List<Task> list = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .list();
        for (Task task : list) {
            System.out.println("任务名称：" + task.getName());
            System.out.println("任务执行人：" + task.getAssignee());
            System.out.println("任务ID：" + task.getId());
            System.out.println("Variables:"+taskService.getVariables(task.getId()));
            System.out.println("VariableLocal"+taskService.getVariablesLocal(task.getId()));
        }
        System.out.println("processVariables:"+processEngine.getRuntimeService().getVariables(processInstanceId));
    }


}
