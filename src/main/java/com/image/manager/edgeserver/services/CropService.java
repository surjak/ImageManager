package com.image.manager.edgeserver.services;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface CropService {
    byte[] crop(byte[] image, Map<String, Integer> params);
}
