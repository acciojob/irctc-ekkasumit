package com.driver.services;

import com.driver.EntryDto.AddTrainEntryDto;
import com.driver.EntryDto.SeatAvailabilityEntryDto;
import com.driver.model.Passenger;
import com.driver.model.Station;
import com.driver.model.Ticket;
import com.driver.model.Train;
import com.driver.repository.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TrainService {

    @Autowired
    TrainRepository trainRepository;

    public Integer addTrain(AddTrainEntryDto trainEntryDto){

        //Add the train to the trainRepository
        //and route String logic to be taken from the Problem statement.
        //Save the train and return the trainId that is generated from the database.
        //Avoid using the lombok library

//        List<Station> stationRoute = trainEntryDto.getStationRoute();
//        String trainRoute = String.join(",", (CharSequence) stationRoute);


        Train train = new Train();

        train.setDepartureTime(trainEntryDto.getDepartureTime());
        train.setNoOfSeats(trainEntryDto.getNoOfSeats());

        List<Station> list = trainEntryDto.getStationRoute();
        String route = "";

        for(int i=0;i<list.size();i++){
                route += list.get(i) + ",";
        }

        String trainRoute = route.substring(0,route.length()-1);
        train.setRoute(trainRoute);

        Train t = trainRepository.save(train);
        return t.getTrainId();
    }

    public Integer calculateAvailableSeats(SeatAvailabilityEntryDto seatAvailabilityEntryDto){

        //Calculate the total seats available
        //Suppose the route is A B C D
        //And there are 2 seats avaialble in total in the train
        //and 2 tickets are booked from A to C and B to D.
        //The seat is available only between A to C and A to B. If a seat is empty between 2 station it will be counted to our final ans
        //even if that seat is booked post the destStation or before the boardingStation
        //Inshort : a train has totalNo of seats and there are tickets from and to different locations
        //We need to find out the available seats between the given 2 stations.

       return null;
    }

    public Integer calculatePeopleBoardingAtAStation(Integer trainId,Station station) throws Exception{

        //We need to find out the number of people who will be boarding a train from a particular station
        //if the trainId is not passing through that station
        //throw new Exception("Train is not passing from this station");
        //  in a happy case we need to find out the number of such people.

        Train train;
        try{
            train = trainRepository.findById(trainId).get();
        }
        catch (Exception e){
            throw new Exception("Invalid Train Id");
        }

//        String givenStation = station.toString();
        boolean flag = false;
        Integer totalPeople = 0;

        for(Ticket t: train.getBookedTickets()){
            if(t.getFromStation() == station){
                flag = true;
                totalPeople += t.getPassengersList().size();
            }
        }

        if(flag == false)
            throw new Exception("Train is not passing from this station");

        return totalPeople;
    }

    public Integer calculateOldestPersonTravelling(Integer trainId){

        //Throughout the journey of the train between any 2 stations
        //We need to find out the age of the oldest person that is travelling the train
        //If there are no people travelling in that train you can return 0
        List<Ticket> bookedTickets = trainRepository.findById(trainId).get().getBookedTickets();
        int maxAge = 0;

        for(Ticket t: bookedTickets){
            List<Passenger> passengers = t.getPassengersList();
            for (Passenger p: passengers){
                if(p.getAge() > maxAge){
                    maxAge = p.getAge();
                }
            }
        }

        return maxAge;
    }

    public List<Integer> trainsBetweenAGivenTime(Station station, LocalTime startTime, LocalTime endTime){

        //When you are at a particular station you need to find out the number of trains that will pass through a given station
        //between a particular time frame both start time and end time included.
        //You can assume that the date change doesn't need to be done ie the travel will certainly happen with the same date (More details
        //in problem statement)
        //You can also assume the seconds and milli seconds value will be 0 in a LocalTime format.

        List<Integer> trainsBtwGivenTime = new ArrayList<>();

        List<Train> trains = trainRepository.findAll();

        for(Train t: trains){

            String[] route = t.getRoute().split(",");

            for(int i=0;i< route.length;i++){
                if(route[i].equals(station.toString())){
                    int startTimeInMin = (startTime.getHour() * 60) + startTime.getMinute();
                    int lastTimeInMin = (endTime.getHour() * 60) + endTime.getMinute();

                    int depTimeInMin = (t.getDepartureTime().getHour() * 60) + t.getDepartureTime().getMinute();
                    int arrTimeInMin  = depTimeInMin + (i * 60);

                    if(arrTimeInMin>=startTimeInMin && arrTimeInMin<=lastTimeInMin)
                        trainsBtwGivenTime.add(t.getTrainId());

                }

            }

        }

        return trainsBtwGivenTime;
    }

}
