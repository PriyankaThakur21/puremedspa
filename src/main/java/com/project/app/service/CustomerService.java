package com.project.app.service;

import com.project.app.entity.Customer;
import com.project.app.repository.CustomerRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.apache.poi.ss.util.DateParser.parseDate;
import static org.apache.poi.ss.util.DateParser.parseLocalDate;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public String create(Customer customer) {
        log.info("Inside create method: CustomerService");
        try {
            Optional<Customer> existByEmail = customerRepository.findByEmail(customer.getEmail());
            if (existByEmail.isPresent()) {
                return "Customer already exist with email: " + customer.getEmail();
            }
            Customer savedCustomer = customerRepository.save(customer);
            log.info("saved{}", savedCustomer);
            return "Customer saved successfully";
        } catch (Exception e) {
            log.error("Error occurred during saving customer{}", e.getMessage());
            return "Exception in saving data" + e.getMessage();
        }
    }

    public String update(Customer customer) {
        log.info("Inside update method: CustomerService");
        try {
            Optional<Customer> existByEmail = customerRepository.findByEmail(customer.getEmail());
            if (existByEmail.isEmpty()) {
                return "Customer not found with email: " + customer.getEmail();
            }
            Customer existingCustomer = existByEmail.get();
            existingCustomer.setFirstName(customer.getFirstName());
            existingCustomer.setLastName(customer.getLastName());
            existingCustomer.setEmail(customer.getEmail());
            existingCustomer.setPhoneNumber(customer.getPhoneNumber());
            existingCustomer.setDateOfBirth(customer.getDateOfBirth());
            existingCustomer.setAddress(customer.getAddress());

            customerRepository.save(existingCustomer);
            return "Customer details updated successfully";
        } catch (Exception e) {
            log.error("Error occurred during updating customer{}", e.getMessage());
            return "Exception in updating data" + e.getMessage();
        }
    }

    public Optional<Customer> fetchCustomerDetails(Customer customer) {
        log.info("Inside getCustomerDetail method: CustomerService");
        try {
            return customerRepository.findByEmail(customer.getEmail());
        } catch (Exception e) {
            log.error("Error occurred during fetching customer{}", e.getMessage());
            return Optional.empty();
        }
    }

    public boolean delete(Customer customer) {
        log.info("Inside delete method: CustomerService");
        try {
            Optional<Customer> existByEmail = customerRepository.findByEmail(customer.getEmail());
            if (existByEmail.isPresent()) {
                customerRepository.delete(customer.getEmail());
                return true;
            } else {
                System.out.println("hiiiii");
                return false;
            }
        } catch (Exception e) {
            log.error("Error occurred during deleting customer{}", e.getMessage());
            return false;
        }
    }

    public String bulkUpload(MultipartFile file) {
        log.info("Inside bulkUpload method: CustomerService");
        try {
            List<Customer> list = new ArrayList<>();
            String fileName = file.getOriginalFilename();
            if (fileName != null && fileName.endsWith(".csv")) {
                // Handle CSV file
                InputStream inputStream = file.getInputStream();
                Reader reader = new InputStreamReader(inputStream);
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader().withIgnoreHeaderCase().withTrim());

                for (CSVRecord record : csvParser) {
                    Customer customer = new Customer();
                    customer.setFirstName(record.get("firstName"));
                    customer.setLastName(record.get("lastName"));
                    customer.setEmail(record.get("email"));
                    customer.setPhoneNumber(record.get("phoneNumber"));
                    customer.setDateOfBirth(parseDate(record.get("dob")).getTime());
                    customer.setAddress(record.get("address"));
                    list.add(customer);
                }
                csvParser.close();
            } else if (fileName != null && fileName.endsWith(".xlsx")) {
                // Handle Excel (XLSX) file
                InputStream inputStream = file.getInputStream();
                Workbook workbook = new XSSFWorkbook(inputStream);
                Sheet sheet = workbook.getSheetAt(0);

                for (Row row : sheet) {
                    if (row.getRowNum() == 0) {
                        continue;
                    }

                    Customer customer = new Customer();
                    customer.setFirstName(row.getCell(0).getStringCellValue());
                    customer.setLastName(row.getCell(1).getStringCellValue());
                    customer.setEmail(row.getCell(2).getStringCellValue());
                    customer.setPhoneNumber(row.getCell(3).getStringCellValue());
                    customer.setDateOfBirth(row.getCell(4).getDateCellValue());
                    customer.setAddress(row.getCell(5).getStringCellValue());
                    list.add(customer);
                }
                workbook.close();
            } else {
                return "Unsupported file format. Please upload a CSV or Excel file.";
            }
            customerRepository.saveAll(list);
            return "Successfully uploaded";
        } catch (Exception e) {
            return "Failed to upload customers: " + e.getMessage();
        }
    }
}
