package com.mobileleader.image.service;

import com.mobileleader.image.model.ConvertRequest;
import com.mobileleader.image.model.ConvertResponse;

public interface ImageConvertService {

	public ConvertResponse convert(ConvertRequest request);
	
}
