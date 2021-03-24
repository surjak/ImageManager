package com.image.manager.edgeserver.services;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface ScaleService {
    byte[] scale(byte[] image, Map<String, Integer> params);
}
