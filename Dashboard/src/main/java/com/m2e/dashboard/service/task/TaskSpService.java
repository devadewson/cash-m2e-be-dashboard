package com.m2e.dashboard.service.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.task.response.TaskDTO;
import com.m2e.dashboard.dto.task.response.TaskStatsDTO;
import com.m2e.dashboard.enums.SPEnum;
import com.m2e.dashboard.sp.BaseSPExecDTO;
import com.m2e.dashboard.sp.maintenance.request.MTUpdateAcctNameSpParam;
import com.m2e.dashboard.sp.task.request.*;
import com.m2e.utils.jdbc.model.dto.BaseSpDto;
import com.m2e.utils.jdbc.service.BaseSpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.m2e.dashboard.enums.SPEnum.SP_ADM_MT_TASK_LIST;
import static com.m2e.dashboard.enums.SPEnum.SP_WD_TASK_LIST_STATS;

@Service
public class TaskSpService {
    private static final Logger log = LoggerFactory.getLogger(TaskSpService.class);

    private final BaseSpService baseSpService;
    private final ObjectMapper objectMapper;

    @Autowired
    public TaskSpService(BaseSpService baseSpService, ObjectMapper objectMapper) {
        this.baseSpService = baseSpService;
        this.objectMapper = objectMapper;
    }

    public SearchCollectionResponse<TaskDTO> getTaskList(TaskListSpParam spParam) {
        log.info("getTaskList param: {}", spParam);

        BaseSpDto<TaskListSpParam, TaskDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_ADM_MT_TASK_LIST.getValue());
        baseSpDto.setParameter(spParam);
        baseSpDto.setPageSize(spParam.getPageSize());
        baseSpDto.setPageNumber(spParam.getPage());
        baseSpDto.setResultClass(TaskDTO.class);

        BaseSpDto<TaskListSpParam, TaskDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);

        List<TaskDTO> spResultList = Optional.ofNullable(resultBaseSpDto)
                .map(BaseSpDto::getResults)
                .orElse(Collections.emptyList());

        log.info("getTaskList spResultList: {}", spResultList);

        SearchCollectionResponse<TaskDTO> result = new SearchCollectionResponse<>();

        int totalPage = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getTotalPage).orElse(0);
        int totalRecordCount = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getTotalRecord).orElse(0);

        result.setNextPage(spParam.getPage() >= totalPage ? totalPage : spParam.getPage() + 1);
        result.setCurrentPage(spParam.getPage());
        result.setTotalPages(totalPage);
        result.setTotalRecordCount(totalRecordCount);
        result.setData(spResultList);

        return result;
    }

    public void insertTask(TaskInsertSpParam spParam) {
        BaseSpDto<TaskInsertSpParam, BaseSPExecDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SPEnum.SP_ADM_MT_INSERT_TASK.getValue());
        baseSpDto.setParameter(spParam);
        BaseSpDto<TaskInsertSpParam, BaseSPExecDTO> resultDto = baseSpService.execute(baseSpDto);

        BaseSPExecDTO spResponse = objectMapper.convertValue(resultDto.getResults().get(0), BaseSPExecDTO.class);
        if (!"0".equals(spResponse.getStatusCode())) {
            throw new RuntimeException("Failed to insert task: " + spResponse.getStatusMessage());
        }
    }

    public void insertTaskVar(TaskVarInsertSpParam spParam) {
        BaseSpDto<TaskVarInsertSpParam, BaseSPExecDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SPEnum.SP_ADM_MT_INSERT_TASK_VAR.getValue());
        baseSpDto.setParameter(spParam);
        BaseSpDto<TaskVarInsertSpParam, BaseSPExecDTO> resultDto = baseSpService.execute(baseSpDto);
        BaseSPExecDTO spResponse = objectMapper.convertValue(resultDto.getResults().get(0), BaseSPExecDTO.class);
        if (!"0".equals(spResponse.getStatusCode())) {
            throw new RuntimeException("Failed to insert task: " + spResponse.getStatusMessage());
        }
    }

    public void insertTaskHistory(TaskHistoryInsertSpParam spParam) {
        BaseSpDto<TaskHistoryInsertSpParam, BaseSPExecDTO> spDto = new BaseSpDto<>();
        spDto.setSpId(SPEnum.SP_ADM_MT_INSERT_TASK_HIS.getValue());
        spDto.setParameter(spParam);
        BaseSpDto<TaskHistoryInsertSpParam, BaseSPExecDTO> resultDto = baseSpService.execute(spDto);
        BaseSPExecDTO spResponse = objectMapper.convertValue(resultDto.getResults().get(0), BaseSPExecDTO.class);
        if (!"0".equals(spResponse.getStatusCode())) {
            throw new RuntimeException("Failed to insert task: " + spResponse.getStatusMessage());
        }
    }

    public void updateTask(TaskUpdateSpParam spParam){
        BaseSpDto<TaskUpdateSpParam, BaseSPExecDTO> spDto = new BaseSpDto<>();
        spDto.setSpId(SPEnum.SP_ADM_MT_UPDATE_TASK.getValue());
        spDto.setParameter(spParam);
        BaseSpDto<TaskUpdateSpParam, BaseSPExecDTO> resultDto = baseSpService.execute(spDto);
        BaseSPExecDTO spResponse = objectMapper.convertValue(resultDto.getResults().get(0), BaseSPExecDTO.class);
        if (!"0".equals(spResponse.getStatusCode())) {
            throw new RuntimeException("Failed to update task: " + spResponse.getStatusMessage());
        }
    }

    public void updateAccountPreferredName(MTUpdateAcctNameSpParam spParam) {
        log.info("updateAccountPreferredName param: {}", spParam);

        if (spParam == null || spParam.getAcctNumber() == null || spParam.getAcctPrefName() == null) {
            log.error("Invalid parameter for account preferred name update: {}", spParam);
            throw new IllegalArgumentException("Invalid parameter for account preferred name update");
        }

        BaseSpDto<MTUpdateAcctNameSpParam, BaseSPExecDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SPEnum.SP_ADM_MT_UPDATE_ACCT_PREF_NM.getValue());
        baseSpDto.setParameter(spParam);

        BaseSpDto<MTUpdateAcctNameSpParam, BaseSPExecDTO> resultDto = baseSpService.execute(baseSpDto);
        BaseSPExecDTO spResponse = objectMapper.convertValue(resultDto.getResults().get(0), BaseSPExecDTO.class);
        if (!"0".equals(spResponse.getStatusCode())) {
            throw new RuntimeException("Failed to insert task: " + spResponse.getStatusMessage());
        }
    }

    public TaskStatsDTO getCountTaskList(TaskListCountSpParam spParam) {
        log.info("getCountTaskList param: {}", spParam);

        BaseSpDto<TaskListCountSpParam, TaskStatsDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_WD_TASK_LIST_STATS.getValue());
        baseSpDto.setParameter(spParam);
        baseSpDto.setResultClass(TaskStatsDTO.class);

        BaseSpDto<TaskListCountSpParam, TaskStatsDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);

        if (resultBaseSpDto.getResults() == null || resultBaseSpDto.getResults().isEmpty()) {
            log.error("Empty SP result -- ");
            TaskStatsDTO taskStatsDTO = new TaskStatsDTO();
            taskStatsDTO.setTotalTasks("0");
            taskStatsDTO.setStatus("Pending Approval");
            return taskStatsDTO;
        }
        log.info("getCountTaskList : {}", resultBaseSpDto);

        return resultBaseSpDto.getResults().get(0);
    }
}
