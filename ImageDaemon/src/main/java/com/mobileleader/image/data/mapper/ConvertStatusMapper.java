package com.mobileleader.image.data.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.mobileleader.image.data.dto.ConvertStatus;

@Mapper
public interface ConvertStatusMapper {

	public List<ConvertStatus> selectConvertFailList();
	
	public int insertBatchConvertRequest(ConvertStatus convertStatus);
	
	public int deleteBatchConvertStatus(@Param("jobId") String jobId);
}
