package com.driver.services;


import com.driver.EntryDto.BookTicketEntryDto;
import com.driver.model.Passenger;
import com.driver.model.Ticket;
import com.driver.model.Train;
import com.driver.repository.PassengerRepository;
import com.driver.repository.TicketRepository;
import com.driver.repository.TrainRepository;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TicketService {

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    TrainRepository trainRepository;

    @Autowired
    PassengerRepository passengerRepository;

    public Integer bookTicket(BookTicketEntryDto bookTicketEntryDto)throws Exception{


//        int bookingPersonId;
//        try {
//            bookingPersonId = passengerRepository.findById(bookTicketEntryDto.getBookingPersonId()).get().getPassengerId();
//        }
//        catch (Exception e){
//            throw new Exception("Invalid Booking Person Id");
//        }


//        List<Passenger> passengersList = new ArrayList<>();
//        try {
//            for(Integer i: bookTicketEntryDto.getPassengerIds()){
//                Passenger passenger = passengerRepository.findById(i).get();
//                passengersList.add(passenger);
//            }
//        }
//        catch (Exception e){
//            throw new Exception("Invalid Passenger Id/s");
//        }

        Train train;
        try {
            train = trainRepository.findById(bookTicketEntryDto.getTrainId()).get();
        }
        catch (Exception e){
            throw new Exception("Invalid Train Id");
        }

        String route = train.getRoute();

        int cnt = 0, depStation = 0, arrStation = 0;
        String[] list = route.split(",");

        for(int i=0;i< list.length;i++){
            if (list[i] == String.valueOf(bookTicketEntryDto.getFromStation())) {
                cnt++;
                depStation = i;
            }
            else if (list[i] == String.valueOf(bookTicketEntryDto.getToStation())) {
                cnt++;
                arrStation = i;
            }
        }

        if(cnt != 2){
            throw new Exception("Invalid Stations");
        }


        List<Passenger> passengersList = new ArrayList<>();
        for(Integer i: bookTicketEntryDto.getPassengerIds()){
            Passenger passenger = passengerRepository.findById(i).get();
            passengersList.add(passenger);
        }


            Ticket ticket=new Ticket();

            ticket.setPassengersList(passengersList);
            ticket.setFromStation(bookTicketEntryDto.getFromStation());
            ticket.setToStation(bookTicketEntryDto.getToStation());

            int fare = (arrStation - depStation)*300*passengersList.size();
            ticket.setTotalFare(fare);

            //set train in ticket
            ticket.setTrain(train);

            for(Passenger p: passengersList){                 //setting ticket for every passenger
                p.getBookedTickets().add(ticket);
            }

            train.getBookedTickets().add(ticket);            //setting ticket in Train class



        //saving train for saving ticket as well as passenger
        trainRepository.save(train);

        return ticket.getTicketId();


        //Check for validity
        //Use bookedTickets List from the TrainRepository to get bookings done against that train
        // Incase the there are insufficient tickets
        // throw new Exception("Less tickets are available");
        //otherwise book the ticket, calculate the price and other details
        //Save the information in corresponding DB Tables
        //Fare System : Check problem statement
        //Incase the train doesn't pass through the requested stations
        //throw new Exception("Invalid stations");
        //Save the bookedTickets in the train Object
        //Also in the passenger Entity change the attribute bookedTickets by using the attribute bookingPersonId.
        //And the end return the ticketId that has come from db

    }




}
