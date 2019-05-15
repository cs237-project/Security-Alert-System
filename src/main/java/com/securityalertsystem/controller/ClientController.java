package com.securityalertsystem.controller;

import com.securityalertsystem.entity.Client;
import com.securityalertsystem.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/client")
public class ClientController {


    @Autowired
    ClientRepository clientRepository;

    @GetMapping(value = "/getClients" )
    public String getClientList(){
        List<Client> clients = clientRepository.findAll();
        String result = "";
        for(Client client:clients){
            result+="<p>Client"+client.getClientId()+"</p>";
        }
        return result;
    }

    @RequestMapping(value = "/addClients/{number}")
    public void addCli(@PathVariable("number") int num){
        for(int i=0;i<num;i++){
            Client cli=new Client();

            cli.setLocationx(50+Math.random()*30);
            cli.setLocationy(50+Math.random()*30);

            cli.setAddressx(51+Math.random()*30);
            cli.setAddressy(51+Math.random()*30);

            clientRepository.save(cli);
        }
    }

}