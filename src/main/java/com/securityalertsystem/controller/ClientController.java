package com.securityalertsystem.controller;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.securityalertsystem.common.Response;
import com.securityalertsystem.entity.Client;
import com.securityalertsystem.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("/client")
public class ClientController {


    @Autowired
    ClientRepository clientRepository;

    @GetMapping(value = "/getClients")
    public Response getClientList(){
        List<Client> clients = clientRepository.findAll();

//        String result = "";
//        for(Client client:clients){
//            result+="ClientId:"+client.getClientId()+ "    location X: " + client.getLocationx() + "    location Y: " + client.getLocationy()
//            + "    address X: " + client.getAddressx() + "    address Y: " + client.getAddressy() + "\n";
//        }
        return Response.createBySuccess("get client successfully",clients);

    }

    @RequestMapping(value = "/addClients/{number}")
    public String addCli(@PathVariable("number") int num){
        for(int i=0;i<num;i++){
            Client cli=new Client();

            cli.setLocationx(50+Math.random()*30);
            cli.setLocationy(50+Math.random()*30);

            cli.setAddressx(51+Math.random()*30);
            cli.setAddressy(51+Math.random()*30);

            clientRepository.save(cli);
        }
        return "successfully added " + num + " clients";
    }

}
