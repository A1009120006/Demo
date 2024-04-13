package org.mys.example.demo.activiti;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.*;
import org.activiti.engine.history.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivitiTest {

    private static ProcessEngine processEngine = null;
    private static RepositoryService repositoryService = null;
    private static DynamicBpmnService dynamicBpmnService = null;
    private static HistoryService historyService = null;
    private static ProcessEngineConfiguration processEngineConfiguration = null;
    private static RuntimeService runtimeService = null;
    private static TaskService taskService = null;
    @BeforeAll
    public static void setup(){
        processEngine = ProcessEngines.getDefaultProcessEngine();
        repositoryService = processEngine.getRepositoryService();
        dynamicBpmnService = processEngine.getDynamicBpmnService();
        historyService = processEngine.getHistoryService();
        processEngineConfiguration = processEngine.getProcessEngineConfiguration();
        runtimeService = processEngine.getRuntimeService();
        taskService = processEngine.getTaskService();
    }
    @Test
    void test1(){
        // 创建流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
//		ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        System.out.println("processEngine = " + processEngine);
        // 得到流程存储服务组件
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 得到运行时服务组件
        RuntimeService runtimeService = processEngine.getRuntimeService();
        // 获取流程任务组件
        TaskService taskService = processEngine.getTaskService();
        // 部署流程定义文件
        repositoryService.createDeployment().addClasspathResource("bpmn/first.bpmn").deploy();
        // 启动流程
        runtimeService.startProcessInstanceByKey("myProcess");
        // 查询第一个任务
        Task task = taskService.createTaskQuery().singleResult();
        System.out.println("第一个任务完成前，当前任务名称:"+task.getName());
        // 完成第一个任务
        taskService.complete(task.getId());
        // 查询第二个任务
        task = taskService.createTaskQuery().singleResult();
        System.out.println("第二个任务完成前，当前任务名称:"+task.getName());
        // 完成第二个任务(流程结束)
        taskService.complete(task.getId());
        task = taskService.createTaskQuery().singleResult();
        System.out.println("流程结束后，查找任务：" + task);

    }

    @Test //部署（Deployment）流程
    void deploy(){
        RepositoryService repositoryService = processEngine.getRepositoryService();

        // 获取仓库服务，从类路径下完成部署
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("processes/test.bpmn20.xml") // 添加定义的规则文件
                .addClasspathResource("processes/test.png") // 添加定义的规则图片
                .name("提交请假流程")  // 部署规则的别名
                .key("test-flow")
                .deploy();
        System.out.println("流程部署id：" + deployment.getId());
        System.out.println("流程部署名称：" + deployment.getName());
        System.out.println("key:"+deployment.getKey());
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
            System.out.println(""+pd.getResourceName());
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
    @Test //开启流程
    public void startProcess_test(){
        //获得流程执行服务类对象
        RuntimeService runServ = processEngine.getRuntimeService();
        //启动流程
        ProcessInstance pi = runServ.startProcessInstanceByKey("test");

        System.out.println(pi.getId()+","+pi.getActivityId()+","+pi.getProcessDefinitionId()+","+pi.getProcessDefinitionKey()
                +","+pi.getProcessDefinitionName()+","+pi.getBusinessKey()+","+pi.getName()+","+pi.getDeploymentId());
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
     * 查看代办任务
     */
    @Test
    public void getTodoTask() {
        //获取一个TaskService对象
        TaskService taskService = processEngine.getTaskService();
        //查询代办业务
        List<Task> list = taskService.createTaskQuery() //查询任务
                .taskAssignee("小红") // 查询待办个人任务
//                .taskCandidateOrAssigned("lisi")  // 查询所有待办任务（个人、组任务）
//                .taskCandidateUser("小红")// 查询待办组任务
                .processDefinitionKey("test") //processDefinitionKey：查询流程
                .list();
        //分页：List<Task> list = taskService.createTaskQuery().taskAssignee("小红").processDefinitionKey("test").listPage(i,j);
        for (Task task : list) {
            System.out.println("任务名称：" + task.getName());
            System.out.println("任务执行人：" + task.getAssignee());
            System.out.println("任务ID：" + task.getId());
            System.out.println("流程实例ID：" + task.getProcessInstanceId());
        }
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
     * 任务处理：完成这一节点任务。当所有任务处理完毕，对应当前流程实例信息删除，但是可以在历史中查看到该信息
     */
    @Test
    public void completeTask() {
        //获取一个TaskService对象
        TaskService taskService = processEngine.getTaskService();
        //任务处理,传任务id
        taskService.complete("1ae429da-0be9-11ee-9e49-38d57a012850");

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
    @Test
    void startProcess(){
        //获得流程执行服务类对象
        RuntimeService runServ = processEngine.getRuntimeService();
        //启动流程的时候【动态设置每个步骤的执行人】,map的key值需要与Leave.bpmn中对应
        Map<String, Object> variables=new HashMap<String, Object>();
        variables.put("user1", "张三");
        variables.put("user2", "李四");
        //启动流程得到流程实例,对应act_ru_execution表
        ProcessInstance pi= runServ.startProcessInstanceByKey("test","aaa", variables);

        System.out.println(pi.getId()+","+pi.getActivityId()+","+pi.getProcessDefinitionId()+","+pi.getProcessDefinitionKey()
                +","+pi.getProcessDefinitionName()+","+pi.getBusinessKey()+","+pi.getName()+","+pi.getDeploymentId());
        assert
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
