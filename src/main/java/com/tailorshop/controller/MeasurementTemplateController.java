// com.tailorshop.controller.MeasurementTemplateController.java
package com.tailorshop.controller;

import com.tailorshop.dao.MeasurementTemplateDao;
import com.tailorshop.DaoImpl.MeasurementTemplateDaoImpl;
import com.tailorshop.model.MeasurementTemplate;

import java.util.List;

public class MeasurementTemplateController {
    private MeasurementTemplateDao dao = new MeasurementTemplateDaoImpl();

    public List<MeasurementTemplate> getAllTemplates() {
        return dao.getAllActive();
    }

    public List<MeasurementTemplate> getTemplatesByCategory(int categoryId) {
        // Untuk kesederhanaan, kembalikan semua ukuran
        // Anda boleh tambah logik khusus mengikut kategori
        return getAllTemplates();
    }

    public int saveTemplate(MeasurementTemplate template) {
        if (template.getFieldName() == null || template.getFieldName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nama medan ukuran diperlukan");
        }
        int id = dao.save(template);
        if (id <= 0) {
            throw new RuntimeException("Gagal menyimpan medan ukuran");
        }
        return id;
    }
}