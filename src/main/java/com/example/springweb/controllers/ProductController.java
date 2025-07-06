//package com.example.springweb.controllers;
//
//import com.example.springweb.models.Product;
//import com.example.springweb.repos.ProductRepo;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//public class ProductController {
//
//    @Autowired
//    ProductRepo product;
//
//    @RequestMapping(value = "/products/",method = RequestMethod.GET)
//    public List<Product> getProducts(){
//        return product.findAll();
//    }
//
//    @RequestMapping(value = "/products/{id}/",method = RequestMethod.GET)
//    public Product getProductById(@PathVariable("id") int id){
//        return product.findById(id).get();
//    }
//
//    @RequestMapping(value = "/products/",method = RequestMethod.POST)
//    public Product createProduct(@RequestBody Product p){
//        return product.save(p);
//    }
//
//    @RequestMapping(value = "/products/", method = RequestMethod.PUT)
//    public Product updateProduct(@RequestBody Product p){
//        return product.save(p);
//    }
//
//    @RequestMapping(value = "/products/{id}",method = RequestMethod.DELETE)
//    public void deleteProduct(@PathVariable("id") int id){
//        product.deleteById(id);
//    }
//}
