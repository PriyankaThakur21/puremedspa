package com.project.app.controller;

import com.project.app.dto.MasterResponseDto;
import com.project.app.entity.Customer;
import com.project.app.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Optional;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerService customerService;

    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/create")
    public ResponseEntity<MasterResponseDto<Customer>> create(@RequestBody Customer customer) {
        try {
            log.info("Attempting to add customer");
            String result = customerService.create(customer);
            if (result.contains("Customer already exist")) {
                log.warn("Customer creation failed: {}", result);
                MasterResponseDto<Customer> masterResponseDto = new MasterResponseDto<>(null, HttpStatus.NOT_FOUND.value(), false, result);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(masterResponseDto);
            }
            MasterResponseDto<Customer> masterResponseDto = new MasterResponseDto<>(null, HttpStatus.CREATED.value(), true, result);
            return ResponseEntity.status(HttpStatus.CREATED).body(masterResponseDto);
        } catch (Exception e) {
            log.error("Error occurred while adding customer: {}", e.getMessage());
            MasterResponseDto<Customer> response = new MasterResponseDto<>(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), false, "An error occurred while adding the customer. Please try again later.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<MasterResponseDto<Customer>> update(@RequestBody Customer customer){
        try{
            log.info("Attempting to update customer details");
            String result = customerService.update(customer);
            if (result.contains("Customer not found")) {
                log.warn("Customer update failed: {}", result);
                MasterResponseDto<Customer> masterResponseDto = new MasterResponseDto<>(null, HttpStatus.NOT_FOUND.value(), false, result);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(masterResponseDto);
            }
            MasterResponseDto<Customer> masterResponseDto = new MasterResponseDto<>(null, HttpStatus.OK.value(), true, result);
            return ResponseEntity.status(HttpStatus.OK).body(masterResponseDto);
        }
        catch(Exception e){
            log.error("Error occurred while updating customer details: {}", e.getMessage());
            MasterResponseDto<Customer> response = new MasterResponseDto<>(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), false, "An error occurred while updating the customer details. Please try again later.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/fetch")
    public ResponseEntity<MasterResponseDto<Customer>> fetch(@RequestBody Customer customer) {
        try {
            log.info("Attempting to fetch customer details");
            Optional<Customer> result = customerService.fetchCustomerDetails(customer);
            if (result.isPresent()) {
                MasterResponseDto<Customer> masterResponseDto = new MasterResponseDto<>(result.get(), HttpStatus.OK.value(), true, "Customer details fetched successfully");
                return ResponseEntity.status(HttpStatus.OK).body(masterResponseDto);
            } else {
                log.warn("Customer not found with email: {}", customer.getEmail());
                MasterResponseDto<Customer> masterResponseDto = new MasterResponseDto<>(null, HttpStatus.NOT_FOUND.value(), false, "Customer not found with the provided email");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(masterResponseDto);
            }
        } catch (Exception e) {
            log.error("Error occurred while fetching customer details: {}", e.getMessage());
            MasterResponseDto<Customer> response = new MasterResponseDto<>(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), false, "An error occurred while fetching customer details. Please try again later.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<MasterResponseDto<Customer>> delete(@RequestBody Customer customer) {
        try {
            log.info("Attempting to delete customer");
            boolean result = customerService.delete(customer);
            if (result) {
                MasterResponseDto<Customer> masterResponseDto = new MasterResponseDto<>(null, HttpStatus.OK.value(), true, "Customer deleted successfully");
                return ResponseEntity.status(HttpStatus.OK).body(masterResponseDto);
            } else {
                log.warn("Customer not found with email: {}", customer.getEmail());
                MasterResponseDto<Customer> masterResponseDto = new MasterResponseDto<>(null, HttpStatus.NOT_FOUND.value(), false, "Customer not found with the provided email");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(masterResponseDto);
            }
        } catch (Exception e) {
            log.error("Error occurred while deleting customer: {}", e.getMessage());
            MasterResponseDto<Customer> response = new MasterResponseDto<>(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), false, "An error occurred while deleting the customer. Please try again later.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/bulk-upload")
    public ResponseEntity<MasterResponseDto<Customer>> bulkUpload(@RequestBody MultipartFile file) {
        try {
            log.info("Attempting to bulk upload customers from file: {}", file.getOriginalFilename());
            String result = customerService.bulkUpload(file);
            if (result.startsWith("Successfully")) {
                MasterResponseDto<Customer> masterResponseDto = new MasterResponseDto<>(null, HttpStatus.CREATED.value(), true, result);
                return ResponseEntity.status(HttpStatus.CREATED).body(masterResponseDto);
            } else {
                MasterResponseDto<Customer> masterResponseDto = new MasterResponseDto<>(null, HttpStatus.BAD_REQUEST.value(), false, result);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(masterResponseDto);
            }
        } catch (Exception e) {
            log.error("Error occurred while uploading bulk customers: {}", e.getMessage());
            MasterResponseDto<Customer> response = new MasterResponseDto<>(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), false, "An error occurred while uploading bulk customers. Please try again later.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }




}
