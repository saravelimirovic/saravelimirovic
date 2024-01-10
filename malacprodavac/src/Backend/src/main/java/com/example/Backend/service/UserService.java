package com.example.Backend.service;

import com.example.Backend.dto.*;
import com.example.Backend.entity.*;
import com.example.Backend.fileSystemImpl.FileSystemUtil;
import com.example.Backend.fileSystemImpl.enums.ImageType;
import com.example.Backend.repository.CityRepository;
import com.example.Backend.repository.StreetRepository;
import com.example.Backend.repository.UserRepository;
import com.example.Backend.security.TokenAuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Consumer;


@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UserService {

    private final MapService mapService;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenAuthenticationService tokenAuthenticationService;

    private final CityRepository cityRepository;
    private final StreetRepository streetRepository;

    private final FileSystemUtil fileSystem;


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }


    @Transactional
    public User createUser(RegisterParam registerParam) throws Exception {
        // Proverite da li korisnik sa istim email-om već postoji
        User existingUser = userRepository.findByEmail(registerParam.getEmail()).orElse(null);
        if (existingUser != null) {
            throw new IllegalArgumentException("User with this email already exists.");
        }

        User newUser = new User();

        if(registerParam.getCity() != null) {
            // Pronađite ili kreirajte grad (City)
            City city = cityRepository.findCityByName(registerParam.getCity()).orElseGet(() -> {
                City newCity = new City();
                newCity.setName(registerParam.getCity());
                return cityRepository.save(newCity);
            });

            // Pronađite ili kreirajte ulicu (Street)
            Street street = streetRepository.findStreetByNameAndCity(registerParam.getStreet(), city).orElseGet(() -> {
                Street newStreet = new Street();
                newStreet.setName(registerParam.getStreet());
                newStreet.setCity(city);
                return streetRepository.save(newStreet);
            });

            newUser.setStreet(street);
            newUser.setPostalCode(registerParam.getPostalCode());

            // Podesite koordinate korisnika
            if (registerParam.getCity() != "" && registerParam.getPostalCode() != "") {
                CoordinatesDTO coordinates = mapService.getCoordinates(registerParam.getCity(), registerParam.getStreet(), registerParam.getStreetNumber(), registerParam.getPostalCode());
                newUser.setLongitude(coordinates.getLongitude());
                newUser.setLatitude(coordinates.getLatitude());
            }

            // Postavite ulicu i broj
            if (registerParam.getStreetNumber().isEmpty()) {
                newUser.setStreetNumber("0");
            } else {
                newUser.setStreetNumber(registerParam.getStreetNumber());
            }
        }

        newUser.setFirstName(registerParam.getFirstName());
        newUser.setLastName(registerParam.getLastName());
        newUser.setEmail(registerParam.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerParam.getPassword()));
        newUser.setPhoneNumber(registerParam.getPhoneNumber());

        // Postavite uloge korisnika
        int role = registerParam.getRole();
        if (role == 1) {
            newUser.setUserRoles(Set.of(UserRoles.CUSTOMER));
        } else if (role == 2) {
            newUser.setUserRoles(Set.of(UserRoles.PRODUCER));
        } else if (role == 3) {
            newUser.setUserRoles(Set.of(UserRoles.DELIVERER));
        }

        User addedUser = userRepository.save(newUser);
        System.out.println(addedUser);

        if(registerParam.getImage() != null) {
            fileSystem.saveImage(String.valueOf(addedUser.getId()), registerParam.getImage(), ImageType.USER);
        }
        // ako nije uneo sliku, postavlja se ona defaultna
        else {
            byte[] defaultImg = Base64.getDecoder().decode("/9j/4AAQSkZJRgABAQAAAQABAAD//gA+Q1JFQVRPUjogZ2QtanBlZyB2MS4wICh1c2luZyBJSkcgSlBFRyB2NjIpLCBkZWZhdWx0IHF1YWxpdHkK/9sAQwAIBgYHBgUIBwcHCQkICgwUDQwLCwwZEhMPFB0aHx4dGhwcICQuJyAiLCMcHCg3KSwwMTQ0NB8nOT04MjwuMzQy/9sAQwEJCQkMCwwYDQ0YMiEcITIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIy/8AAEQgCWAJYAwEiAAIRAQMRAf/EAB8AAAEFAQEBAQEBAAAAAAAAAAABAgMEBQYHCAkKC//EALUQAAIBAwMCBAMFBQQEAAABfQECAwAEEQUSITFBBhNRYQcicRQygZGhCCNCscEVUtHwJDNicoIJChYXGBkaJSYnKCkqNDU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6g4SFhoeIiYqSk5SVlpeYmZqio6Slpqeoqaqys7S1tre4ubrCw8TFxsfIycrS09TV1tfY2drh4uPk5ebn6Onq8fLz9PX29/j5+v/EAB8BAAMBAQEBAQEBAQEAAAAAAAABAgMEBQYHCAkKC//EALURAAIBAgQEAwQHBQQEAAECdwABAgMRBAUhMQYSQVEHYXETIjKBCBRCkaGxwQkjM1LwFWJy0QoWJDThJfEXGBkaJicoKSo1Njc4OTpDREVGR0hJSlNUVVZXWFlaY2RlZmdoaWpzdHV2d3h5eoKDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uLj5OXm5+jp6vLz9PX29/j5+v/aAAwDAQACEQMRAD8A9iooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAoo/OkJCjJ4HrQAtFUptVtIf+Wm4+i81Ql8Qdo4PxLf/WoA3PwormH1q7c9VX6CoG1C7Y8zv+dAHXUVxpuJj1lb86Tzpf77fnQB2dFcaLmdekrD8alXUbtOk7n6mgDraK5lNbul+9tYe4q5F4gU8SQEe6tn+lAG1RVSHU7Wb7sgB9G4NW+tABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFNd1jUs7BVHUmgB1RT3MVuu6VgB2rIvdbP3LZf+Bn+grGeRpWLOxYnuaANi514k7bePA/vN/hWVNczXDZkkLfjUVFABRRRQAUUUUAFFFFABRRRQAUUUUAFTwXk9sf3bkD07VBRQBu2+vKfluEx/tL/AIVrRTRzJujYMPUVxlPimkgfdExVvagDtKKx7PW0fCXA2N/e7VrghlBByO1AC0UUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRWdqOpraDYnzSnoPSgCe8vorNMvyx6L61zV3ey3j7pDgdlHQVC7tK5dySx6k02gAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAq5ZajLZtgfNH3X/AAqnRQB2FtdxXce+I/UdxU9cZBPJbyCSMkMK6ex1CO8Tj5XHVaALlFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFUtRvxZQ8cyN90UAQ6pqQtV8qPmU/pXOMSxJJJJ6mhmZ3LMck9TSUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAU6ORoXDodrDvTaKAOp07UFvY8HiVeo9avVxcMzwSiRDhhXV2d2l5AJF4PdfSgCzRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUARTzpbwtK/Rf1rkrm4e5maR+p7elXtYvPOnEKn5I/1NZlABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAVZsbxrO4DjJU8MPUVWooA7VXEiB15U9KdWFod5gm2c+6/4Vu0AFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFU9SuvstqWBw7cL9auVzGsXPn3mwH5Y+B9e9AGfRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFADkdo3DqcMORXX2twtzbrKvfqPSuOrY0K52SvAx4bkfWgDfooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigCC7n+z20kvcDj61yBJJJPWt3XpsRRQj+Ikn8MVgigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAp8MrQyrIvVTmmUd6AO1jcSRq46MMinVm6LN5llsPVDj8K0qACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooA5fWJfM1Bh2X5aoU+aTzJ3kP8RzTKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigDW0GXbdPH2Zc/l/8Arroa5LTpPLv4j6tiutoAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACorl9ltI/oualqrqJ26fP7qaAOSooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKAHRtskVvQ5rtfpXEV2kRzCh9RQA+iiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAqlqn/IPl+lXaqamM6fN/umgDk6KKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigArsrb/j1j/3a42uzgGIIx7UASUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFQXi7rOVR3U1PSEBgQehoA4mildSjlT1BxSUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFACgEkAdTXagbQB6VyFknmXkS+rCuwoAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigDk9Ti8q/lHqciqlbOvxYkim9QQfwrGoAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKANLRIt99u7IM10tZGgw7YJJT/GQPy//AF1r0AFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQBT1OD7RYuAMsPmH1rlK7euRv7f7Ldun8PUfSgCtRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABQAScDk9hRWho9sZ7wMfux8mgDobWEW9ukY/hGDU1FFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFZWt2vm24mUfNHn8RWrSFQwIIyD2oA4mirV/aG0uSn8J5U+1VaACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAK6rS7T7LaDcPnblqxdIs/tNzvYfJHyfrXT0AFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFAFPUbIXlvtHDryprlWUqSrDBHb0rtqxtY0/cPtMXUffH9aAMGiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAp8UTTSLGgyx4pldJpOn/Zk82QfvW7f3RQBctLZbW3ES9up9TU9FFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAc7qum+SxnhH7s9V9Kyq7YgEEEZB7Vz2paUYCZYBmPuvpQBlUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRR+FbemaTgie4H+6tAC6TphGLibr/AAqa26P6UUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFAGNf6MH/e23Dd09fpWEylSVYYI657V21VLzT4bwZbhx0YUAcnRVu70+azOWG5P7wqpQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABTo4nmcIilmPartnpc11hj8kfqR1roLWzhs02xDnuT1NAFSw0lbfEk3zSemOBWnRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAhAIIIyD1HrWXdaJDNloT5bemMg1q0UAcjcWFxbH50O31HSq1dvVOfTLWfkx7W9V60AcpRWzNoDj/AFMob2YYqjLpt3F96Ikeq80AVKKVlKHDAg+9JQAUUUUAFFFFABRTkR5DhQSfYVai0q7l6RFf97igCnRW3DoHeab8FH9a0oNPtrc5SMZH8R5NAHPW2mXNzyF2r/eatq10eC3O5/3j+pHStGigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKAEZQwwRkVA1lauctAh+oqxRQBTOl2Z/wCWI/Kk/sqz/wCeVXaKAKa6ZZj/AJYKalWztk+7Ag+gqeigBAABgDFLRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUVBJeW8XDzKPbNVH1u0Tpub6CgDSorFbxAo+5bk/V8f0qFtfmP3YVH1OaAOgormzrt12CflSf25ef9M/++aAOlormxrt13VPyqVdflB+aFT9GxQBv0VjL4gQ/egK/Rs/0qxHrNo/dl+ooA0aKhjuoJf9XKrfQ1NQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRTZJEiTdIwUeprIuddVflt03H+8TxQBsEgDJOPqao3Gr2sHAbe3otc9cXc9ycyuT7dhUFAGtPrsznESCMe5yaz5ruefiSVmHoTUNFABRRRQAUUUUAFFFFABRRRQAUUUUAFWIb64g+5KQPTPFV6KANiHXpF4miDD1U4rSt9UtbjgPtY9mrlaKAO37d6Otcjb31xa/6tzj+6elbFrrkUuFnXy29c5BoA1qKRXV13KQQe9L3oAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKrXd9DZrukPzHoo6mgCwSAMk4FZN5rcceVtxvb1zxWVeajNeNg/Kn90VUoAlnuJbh90rlj71FiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAntrya1bMTED0PSt2z1iK4+WUeW/14Nc3RQB2+aK5ey1WW0+Rvnj9M9PpXRW91FdJvjbI7+ooAmooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKOtFYWpasWzDbnA/icd6ALOoautuTHDhpO57LXPSSPI5eQksepNNz3ooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACpIJ5LeQPGxDfzqOigDp7DU47wbW+SX0z1rQriQSpyDg+tb+m6sJcQznD/wt60Aa9FFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUZAGT09aKwNW1PzCbeE/L/E3r7UAN1TVPOzDCcR9z61k0UUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFAG5peq5xBOef4WNbdcRW/pOpeZi3mPz/AMLevtQBsUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUVS1K9FnBkcu3Cj+tAFXV9R8lfIiPzn7x9BXP0rMzsWY5J5JpKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKASCCDgiiigDp9Lv/tkex/8AWr1960K4uKV4ZVkQ4YGuss7pbuASKMHoV9DQBYooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKAGSyLFG0jnCr1rkru5a7uDK34D0rR1u83yC2Q/Kv3vc1j0AFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABVzTrw2dxk8o3DCqdFAHbAhhkdDS1jaJebkNs55XlfpWzQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAVWvroWls0nfoo9TVmua1m68668sH5Y+PxoAzmJZiSSSeppKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAfFK0MqyJ95TkV18Ey3EKyp0YZrja2dCutrtbt/Fyv1oA3qKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAr3tx9ltXk7jp9a5Akk5PPetjXbjdIkAPC5Y/jWPQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAU+KRopFkU/MDmmUUAdnDKs0SSL0YZqSsfQrjfE8DH7vIrYoAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACkYhVJPAHWlqjq03k2D46v8tAHNXExuLh5T1Y5qOiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAtadP9nvUb+EnDfSutriK6+ym+0WkcncjmgCxRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAVg6/LmSKIHoCT+Nb1cpqknm6hKf7p20AU+9FFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAV0Ggy7reSL+4Qfz/wD1Vz9aeiSbL0qejrQB0lFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFACE4BJri3Yu5Y9+a629fZZyt6Ka5CgAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACrFjJ5d7E3+0Kr0oOCD6UAdtRSKdygjuKWgAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigCjq7bdOk9+K5aiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigDsLNt9nE3qoqeiigAooooAKKKKACiiigAooooAKKKKACiiigD//2Q==");
            fileSystem.saveImage(String.valueOf(addedUser.getId()), defaultImg, ImageType.USER);
        }

        return addedUser;
    }

    @Transactional(readOnly = true)
    public void restoreTokens() {

        Map<String, String> tokens = userRepository.findAll()
                .stream()
                .filter(user -> user.getTokens() != null && !user.getTokens()
                        .isEmpty())
                .flatMap(user -> user.getTokens()
                        .stream())
                .collect(HashMap::new, (m, v) -> m.put(v.getToken(), v.getCredentials()), HashMap::putAll);

        tokenAuthenticationService.setWebActiveTokens(tokens);
    }

//    public boolean test(Korisnik korisnik) {
//        Korisnik korisnik1 = korisnikRepository.findById(korisnik.getId())
//                .orElseThrow(() -> new IllegalArgumentException("nesto"));
//
//        return korisnik.equals(korisnik1);
//    }


    @Transactional(readOnly = true)
    public List<CustomerMapDTO> getCustomersLocation() {
        return userRepository.findAllUsersWithLocation();
    }

    @Transactional(readOnly = true)
    public User getUserById(Long userId, Consumer<User> init) {
        Optional<User> user = userRepository.findUserById(userId);

        if(init != null) {
            user.ifPresent(init);
        }

        return user.orElse(null);
    }

    @Modifying
    public User updateUser(Long userId, UsersProfileDTO userObj) {
        User user = userRepository.findUserById(userId).orElse(null);

        if(Objects.isNull(userObj)) {
            return null;
        }
        else {
            Street userStreet = streetRepository.findStreetById(user.getStreet().getId()).orElse(null);

            City city = cityRepository.findCityByName(userObj.getCityName()).orElse(null);
            // ako ne postoji grad
            if(city == null) {
                City newCity = new City();
                newCity.setName(userObj.getCityName());
                newCity = cityRepository.save(newCity);

                Street newStreet = new Street();
                newStreet.setName(userObj.getStreetName());
                newStreet.setCity(newCity);
                newStreet = streetRepository.save(newStreet);

                user.setStreet(newStreet);
            }
            // ako postoji grad
            else {
                Street street = streetRepository.findStreetByNameAndCity(userObj.getStreetName(), city).orElse(null);
                if (street == null)
                {
                    Street newStreet = new Street();
                    newStreet.setName(userObj.getStreetName());
                    newStreet.setCity(city);
                    newStreet = streetRepository.save(newStreet);

                    user.setStreet(newStreet);
                }
                else if (!userStreet.getName().equals(userObj.getStreetName()))
                {
                    user.setStreet(street);
                }
            }

            String[] fullName = userObj.getFullName().split(" ");
            String firstName = fullName[0];
            String lastName = fullName[1];


            if(firstName != null && !user.getFirstName().equals(firstName))
                user.setFirstName(firstName);
            if(lastName != null && !user.getLastName().equals(lastName))
                user.setLastName(lastName);
            if(userObj.getEmail() != null && !user.getEmail().equals(userObj.getEmail()))
                user.setEmail(userObj.getEmail());
            if(!Objects.equals(user.getPostalCode(), userObj.getPostalCode()))
                user.setPostalCode(userObj.getPostalCode());
            if(user.getLongitude() != userObj.getLongitude())
                user.setLongitude(userObj.getLongitude());
            if(user.getLatitude() != userObj.getLatitude())
                user.setLatitude(userObj.getLatitude());
            if(!Objects.equals(user.getAge(), userObj.getAge()))
                user.setAge(userObj.getAge());
            if(userObj.getPhoneNumber() != null && user.getPhoneNumber().equals(userObj.getPhoneNumber()))
                user.setPhoneNumber(userObj.getPhoneNumber());

//            Rola rola = await _rolaRepository.getRolaById(person.RolaId);
//            if (!rola.Equals(userObj.Rola))
//            {
//                Rola newRola = await _rolaRepository.getRolaByNaziv(userObj.Rola);
//                person.RolaId = newRola.Id;
//            }

        }

        return userRepository.save(user);
    }

    @Modifying
    public User updateMyInfoUser(Long userId, MyInfoUpdateDTO userInfo) {
        User user = userRepository.findUserById(userId).orElse(null);

        if(Objects.isNull(userInfo)) {
            return null;
        }
        else {
            user.setFirstName(userInfo.getFirstName());
            user.setLastName(userInfo.getLastName());
            user.setPhoneNumber(userInfo.getPhoneNumber());
            if(userInfo.getImage() != null)
                fileSystem.saveImage(String.valueOf(user.getId()), userInfo.getImage(), ImageType.USER);
            else {
                byte[] defaultImg = Base64.getDecoder().decode("/9j/4AAQSkZJRgABAQAAAQABAAD//gA+Q1JFQVRPUjogZ2QtanBlZyB2MS4wICh1c2luZyBJSkcgSlBFRyB2NjIpLCBkZWZhdWx0IHF1YWxpdHkK/9sAQwAIBgYHBgUIBwcHCQkICgwUDQwLCwwZEhMPFB0aHx4dGhwcICQuJyAiLCMcHCg3KSwwMTQ0NB8nOT04MjwuMzQy/9sAQwEJCQkMCwwYDQ0YMiEcITIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIy/8AAEQgCWAJYAwEiAAIRAQMRAf/EAB8AAAEFAQEBAQEBAAAAAAAAAAABAgMEBQYHCAkKC//EALUQAAIBAwMCBAMFBQQEAAABfQECAwAEEQUSITFBBhNRYQcicRQygZGhCCNCscEVUtHwJDNicoIJChYXGBkaJSYnKCkqNDU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6g4SFhoeIiYqSk5SVlpeYmZqio6Slpqeoqaqys7S1tre4ubrCw8TFxsfIycrS09TV1tfY2drh4uPk5ebn6Onq8fLz9PX29/j5+v/EAB8BAAMBAQEBAQEBAQEAAAAAAAABAgMEBQYHCAkKC//EALURAAIBAgQEAwQHBQQEAAECdwABAgMRBAUhMQYSQVEHYXETIjKBCBRCkaGxwQkjM1LwFWJy0QoWJDThJfEXGBkaJicoKSo1Njc4OTpDREVGR0hJSlNUVVZXWFlaY2RlZmdoaWpzdHV2d3h5eoKDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uLj5OXm5+jp6vLz9PX29/j5+v/aAAwDAQACEQMRAD8A9iooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAoo/OkJCjJ4HrQAtFUptVtIf+Wm4+i81Ql8Qdo4PxLf/WoA3PwormH1q7c9VX6CoG1C7Y8zv+dAHXUVxpuJj1lb86Tzpf77fnQB2dFcaLmdekrD8alXUbtOk7n6mgDraK5lNbul+9tYe4q5F4gU8SQEe6tn+lAG1RVSHU7Wb7sgB9G4NW+tABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFNd1jUs7BVHUmgB1RT3MVuu6VgB2rIvdbP3LZf+Bn+grGeRpWLOxYnuaANi514k7bePA/vN/hWVNczXDZkkLfjUVFABRRRQAUUUUAFFFFABRRRQAUUUUAFTwXk9sf3bkD07VBRQBu2+vKfluEx/tL/AIVrRTRzJujYMPUVxlPimkgfdExVvagDtKKx7PW0fCXA2N/e7VrghlBByO1AC0UUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRWdqOpraDYnzSnoPSgCe8vorNMvyx6L61zV3ey3j7pDgdlHQVC7tK5dySx6k02gAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAq5ZajLZtgfNH3X/AAqnRQB2FtdxXce+I/UdxU9cZBPJbyCSMkMK6ex1CO8Tj5XHVaALlFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFUtRvxZQ8cyN90UAQ6pqQtV8qPmU/pXOMSxJJJJ6mhmZ3LMck9TSUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAU6ORoXDodrDvTaKAOp07UFvY8HiVeo9avVxcMzwSiRDhhXV2d2l5AJF4PdfSgCzRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUARTzpbwtK/Rf1rkrm4e5maR+p7elXtYvPOnEKn5I/1NZlABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAVZsbxrO4DjJU8MPUVWooA7VXEiB15U9KdWFod5gm2c+6/4Vu0AFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFU9SuvstqWBw7cL9auVzGsXPn3mwH5Y+B9e9AGfRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFADkdo3DqcMORXX2twtzbrKvfqPSuOrY0K52SvAx4bkfWgDfooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigCC7n+z20kvcDj61yBJJJPWt3XpsRRQj+Ikn8MVgigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAp8MrQyrIvVTmmUd6AO1jcSRq46MMinVm6LN5llsPVDj8K0qACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooA5fWJfM1Bh2X5aoU+aTzJ3kP8RzTKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigDW0GXbdPH2Zc/l/8Arroa5LTpPLv4j6tiutoAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACorl9ltI/oualqrqJ26fP7qaAOSooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKAHRtskVvQ5rtfpXEV2kRzCh9RQA+iiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAqlqn/IPl+lXaqamM6fN/umgDk6KKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigArsrb/j1j/3a42uzgGIIx7UASUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFQXi7rOVR3U1PSEBgQehoA4mildSjlT1BxSUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFACgEkAdTXagbQB6VyFknmXkS+rCuwoAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigDk9Ti8q/lHqciqlbOvxYkim9QQfwrGoAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKANLRIt99u7IM10tZGgw7YJJT/GQPy//AF1r0AFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQBT1OD7RYuAMsPmH1rlK7euRv7f7Ldun8PUfSgCtRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABQAScDk9hRWho9sZ7wMfux8mgDobWEW9ukY/hGDU1FFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFZWt2vm24mUfNHn8RWrSFQwIIyD2oA4mirV/aG0uSn8J5U+1VaACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAK6rS7T7LaDcPnblqxdIs/tNzvYfJHyfrXT0AFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFAFPUbIXlvtHDryprlWUqSrDBHb0rtqxtY0/cPtMXUffH9aAMGiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAp8UTTSLGgyx4pldJpOn/Zk82QfvW7f3RQBctLZbW3ES9up9TU9FFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAc7qum+SxnhH7s9V9Kyq7YgEEEZB7Vz2paUYCZYBmPuvpQBlUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRR+FbemaTgie4H+6tAC6TphGLibr/AAqa26P6UUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFAGNf6MH/e23Dd09fpWEylSVYYI657V21VLzT4bwZbhx0YUAcnRVu70+azOWG5P7wqpQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABTo4nmcIilmPartnpc11hj8kfqR1roLWzhs02xDnuT1NAFSw0lbfEk3zSemOBWnRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAhAIIIyD1HrWXdaJDNloT5bemMg1q0UAcjcWFxbH50O31HSq1dvVOfTLWfkx7W9V60AcpRWzNoDj/AFMob2YYqjLpt3F96Ikeq80AVKKVlKHDAg+9JQAUUUUAFFFFABRTkR5DhQSfYVai0q7l6RFf97igCnRW3DoHeab8FH9a0oNPtrc5SMZH8R5NAHPW2mXNzyF2r/eatq10eC3O5/3j+pHStGigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKAEZQwwRkVA1lauctAh+oqxRQBTOl2Z/wCWI/Kk/sqz/wCeVXaKAKa6ZZj/AJYKalWztk+7Ag+gqeigBAABgDFLRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUVBJeW8XDzKPbNVH1u0Tpub6CgDSorFbxAo+5bk/V8f0qFtfmP3YVH1OaAOgormzrt12CflSf25ef9M/++aAOlormxrt13VPyqVdflB+aFT9GxQBv0VjL4gQ/egK/Rs/0qxHrNo/dl+ooA0aKhjuoJf9XKrfQ1NQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRTZJEiTdIwUeprIuddVflt03H+8TxQBsEgDJOPqao3Gr2sHAbe3otc9cXc9ycyuT7dhUFAGtPrsznESCMe5yaz5ruefiSVmHoTUNFABRRRQAUUUUAFFFFABRRRQAUUUUAFWIb64g+5KQPTPFV6KANiHXpF4miDD1U4rSt9UtbjgPtY9mrlaKAO37d6Otcjb31xa/6tzj+6elbFrrkUuFnXy29c5BoA1qKRXV13KQQe9L3oAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKrXd9DZrukPzHoo6mgCwSAMk4FZN5rcceVtxvb1zxWVeajNeNg/Kn90VUoAlnuJbh90rlj71FiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAntrya1bMTED0PSt2z1iK4+WUeW/14Nc3RQB2+aK5ey1WW0+Rvnj9M9PpXRW91FdJvjbI7+ooAmooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKOtFYWpasWzDbnA/icd6ALOoautuTHDhpO57LXPSSPI5eQksepNNz3ooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACpIJ5LeQPGxDfzqOigDp7DU47wbW+SX0z1rQriQSpyDg+tb+m6sJcQznD/wt60Aa9FFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUZAGT09aKwNW1PzCbeE/L/E3r7UAN1TVPOzDCcR9z61k0UUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFAG5peq5xBOef4WNbdcRW/pOpeZi3mPz/AMLevtQBsUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUVS1K9FnBkcu3Cj+tAFXV9R8lfIiPzn7x9BXP0rMzsWY5J5JpKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKASCCDgiiigDp9Lv/tkex/8AWr1960K4uKV4ZVkQ4YGuss7pbuASKMHoV9DQBYooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKAGSyLFG0jnCr1rkru5a7uDK34D0rR1u83yC2Q/Kv3vc1j0AFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABVzTrw2dxk8o3DCqdFAHbAhhkdDS1jaJebkNs55XlfpWzQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAVWvroWls0nfoo9TVmua1m68668sH5Y+PxoAzmJZiSSSeppKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAfFK0MqyJ95TkV18Ey3EKyp0YZrja2dCutrtbt/Fyv1oA3qKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAr3tx9ltXk7jp9a5Akk5PPetjXbjdIkAPC5Y/jWPQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAU+KRopFkU/MDmmUUAdnDKs0SSL0YZqSsfQrjfE8DH7vIrYoAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACkYhVJPAHWlqjq03k2D46v8tAHNXExuLh5T1Y5qOiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAtadP9nvUb+EnDfSutriK6+ym+0WkcncjmgCxRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAVg6/LmSKIHoCT+Nb1cpqknm6hKf7p20AU+9FFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAV0Ggy7reSL+4Qfz/wD1Vz9aeiSbL0qejrQB0lFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFACE4BJri3Yu5Y9+a629fZZyt6Ka5CgAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACrFjJ5d7E3+0Kr0oOCD6UAdtRSKdygjuKWgAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigCjq7bdOk9+K5aiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigDsLNt9nE3qoqeiigAooooAKKKKACiiigAooooAKKKKACiiigD//2Q==");
                fileSystem.saveImage(String.valueOf(user.getId()), defaultImg, ImageType.USER);
            }

            return userRepository.save(user);
        }
    }

    public MyInfoUpdateDTO getMyInfo(Long userId) {
        User user = userRepository.findUserById(userId).orElse(null);

        if(user == null) {
            return null;
        }
        else {
            byte[] image = fileSystem.getImageInBytes(String.valueOf(user.getId()), ImageType.USER);
            MyInfoUpdateDTO dto = new MyInfoUpdateDTO(user.getFirstName(), user.getLastName(), user.getPhoneNumber(), image);
            return dto;
        }
    }

    public MyLocationUpdateDTO getMyLocation(Long userId) {
        User user = userRepository.findUserById(userId).orElse(null);

        if(user == null) {
            return null;
        }
        else {
            Street street = Optional.ofNullable(user.getStreet())
                    .map(street1 -> streetRepository.findStreetById(street1.getId()).orElse(null))
                    .orElse(null);

            City city = Optional.ofNullable(user)
                    .map(User::getStreet)
                    .flatMap(street1 -> Optional.ofNullable(street1.getCity()))
                    .map(city1 -> cityRepository.findCityById(city1.getId()).orElse(null))
                    .orElse(null);

            MyLocationUpdateDTO dto = new MyLocationUpdateDTO(city.getName(), user.getPostalCode(), street.getName(), user.getStreetNumber(), user.getLongitude(), user.getLatitude());
            return dto;
        }
    }

    public User updateMyLocation(Long userId, MyLocationUpdateDTO locationUpdateDTO) throws Exception {
        User user = userRepository.findUserById(userId).orElse(null);

        if(user == null) {
            return null;
        }
        else {
            Street userStreet = new Street();
            if(user.getStreet() != null)
                userStreet = streetRepository.findStreetById(user.getStreet().getId()).orElse(null);

            City city = cityRepository.findCityByName(locationUpdateDTO.getCity()).orElse(null);
            // ako ne postoji grad
            if(city == null) {
                City newCity = new City();
                newCity.setName(locationUpdateDTO.getCity());
                newCity = cityRepository.save(newCity);

                Street newStreet = new Street();
                newStreet.setName(locationUpdateDTO.getStreet());
                newStreet.setCity(newCity);
                newStreet = streetRepository.save(newStreet);

                user.setStreet(newStreet);
            }
            // ako postoji grad
            else {
                Street street = streetRepository.findStreetByNameAndCity(locationUpdateDTO.getStreet(), city).orElse(null);
                // ako ne postoji ta ulica
                if (street == null)
                {
                    Street newStreet = new Street();
                    newStreet.setName(locationUpdateDTO.getStreet());
                    newStreet.setCity(city);
                    newStreet = streetRepository.save(newStreet);

                    user.setStreet(newStreet);
                }
                // ako postoji
                else {
                    user.setStreet(street);
                }
            }
        }
        user.setPostalCode(locationUpdateDTO.getPostalCode());
        user.setStreetNumber(locationUpdateDTO.getStreetNumber());
        //lokacija update
        System.out.println(locationUpdateDTO.getLongitude() + " : " + locationUpdateDTO.getLatitude());
        if (locationUpdateDTO.getLatitude() != 0.0 && locationUpdateDTO.getLongitude() != 0.0) {
            user.setLongitude(locationUpdateDTO.getLongitude());
            user.setLatitude(locationUpdateDTO.getLatitude());
        }
        else {
            CoordinatesDTO coordinatesDTO = mapService.getCoordinates(locationUpdateDTO.getCity(), locationUpdateDTO.getStreet(), locationUpdateDTO.getStreetNumber(), locationUpdateDTO.getPostalCode());
            user.setLatitude(coordinatesDTO.getLatitude());
            user.setLongitude(coordinatesDTO.getLongitude());
        }
        return userRepository.save(user);
    }

    @Modifying
    public boolean updateProfilePic(Long userId, UpdateProfilePicDTO request) {
        User user = userRepository.findUserById(userId).orElse(null);

        if(user == null) {
            return false;
        }
        else {
            if(request.getImage() != null) {
                fileSystem.saveImage(String.valueOf(user.getId()), request.getImage(), ImageType.USER);

                return true;
            }
            // ako nije uneo sliku, iliti poslato je null, to znaci da ipak sad zeli da ostane bez slike
            else {
                byte[] defaultImg = Base64.getDecoder().decode("/9j/4AAQSkZJRgABAQAAAQABAAD//gA+Q1JFQVRPUjogZ2QtanBlZyB2MS4wICh1c2luZyBJSkcgSlBFRyB2NjIpLCBkZWZhdWx0IHF1YWxpdHkK/9sAQwAIBgYHBgUIBwcHCQkICgwUDQwLCwwZEhMPFB0aHx4dGhwcICQuJyAiLCMcHCg3KSwwMTQ0NB8nOT04MjwuMzQy/9sAQwEJCQkMCwwYDQ0YMiEcITIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIy/8AAEQgCWAJYAwEiAAIRAQMRAf/EAB8AAAEFAQEBAQEBAAAAAAAAAAABAgMEBQYHCAkKC//EALUQAAIBAwMCBAMFBQQEAAABfQECAwAEEQUSITFBBhNRYQcicRQygZGhCCNCscEVUtHwJDNicoIJChYXGBkaJSYnKCkqNDU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6g4SFhoeIiYqSk5SVlpeYmZqio6Slpqeoqaqys7S1tre4ubrCw8TFxsfIycrS09TV1tfY2drh4uPk5ebn6Onq8fLz9PX29/j5+v/EAB8BAAMBAQEBAQEBAQEAAAAAAAABAgMEBQYHCAkKC//EALURAAIBAgQEAwQHBQQEAAECdwABAgMRBAUhMQYSQVEHYXETIjKBCBRCkaGxwQkjM1LwFWJy0QoWJDThJfEXGBkaJicoKSo1Njc4OTpDREVGR0hJSlNUVVZXWFlaY2RlZmdoaWpzdHV2d3h5eoKDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uLj5OXm5+jp6vLz9PX29/j5+v/aAAwDAQACEQMRAD8A9iooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAoo/OkJCjJ4HrQAtFUptVtIf+Wm4+i81Ql8Qdo4PxLf/WoA3PwormH1q7c9VX6CoG1C7Y8zv+dAHXUVxpuJj1lb86Tzpf77fnQB2dFcaLmdekrD8alXUbtOk7n6mgDraK5lNbul+9tYe4q5F4gU8SQEe6tn+lAG1RVSHU7Wb7sgB9G4NW+tABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFNd1jUs7BVHUmgB1RT3MVuu6VgB2rIvdbP3LZf+Bn+grGeRpWLOxYnuaANi514k7bePA/vN/hWVNczXDZkkLfjUVFABRRRQAUUUUAFFFFABRRRQAUUUUAFTwXk9sf3bkD07VBRQBu2+vKfluEx/tL/AIVrRTRzJujYMPUVxlPimkgfdExVvagDtKKx7PW0fCXA2N/e7VrghlBByO1AC0UUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRWdqOpraDYnzSnoPSgCe8vorNMvyx6L61zV3ey3j7pDgdlHQVC7tK5dySx6k02gAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAq5ZajLZtgfNH3X/AAqnRQB2FtdxXce+I/UdxU9cZBPJbyCSMkMK6ex1CO8Tj5XHVaALlFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFUtRvxZQ8cyN90UAQ6pqQtV8qPmU/pXOMSxJJJJ6mhmZ3LMck9TSUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAU6ORoXDodrDvTaKAOp07UFvY8HiVeo9avVxcMzwSiRDhhXV2d2l5AJF4PdfSgCzRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUARTzpbwtK/Rf1rkrm4e5maR+p7elXtYvPOnEKn5I/1NZlABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAVZsbxrO4DjJU8MPUVWooA7VXEiB15U9KdWFod5gm2c+6/4Vu0AFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFU9SuvstqWBw7cL9auVzGsXPn3mwH5Y+B9e9AGfRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFADkdo3DqcMORXX2twtzbrKvfqPSuOrY0K52SvAx4bkfWgDfooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigCC7n+z20kvcDj61yBJJJPWt3XpsRRQj+Ikn8MVgigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAp8MrQyrIvVTmmUd6AO1jcSRq46MMinVm6LN5llsPVDj8K0qACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooA5fWJfM1Bh2X5aoU+aTzJ3kP8RzTKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigDW0GXbdPH2Zc/l/8Arroa5LTpPLv4j6tiutoAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACorl9ltI/oualqrqJ26fP7qaAOSooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKAHRtskVvQ5rtfpXEV2kRzCh9RQA+iiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAqlqn/IPl+lXaqamM6fN/umgDk6KKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigArsrb/j1j/3a42uzgGIIx7UASUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFQXi7rOVR3U1PSEBgQehoA4mildSjlT1BxSUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFACgEkAdTXagbQB6VyFknmXkS+rCuwoAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigDk9Ti8q/lHqciqlbOvxYkim9QQfwrGoAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKANLRIt99u7IM10tZGgw7YJJT/GQPy//AF1r0AFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQBT1OD7RYuAMsPmH1rlK7euRv7f7Ldun8PUfSgCtRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABQAScDk9hRWho9sZ7wMfux8mgDobWEW9ukY/hGDU1FFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFZWt2vm24mUfNHn8RWrSFQwIIyD2oA4mirV/aG0uSn8J5U+1VaACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAK6rS7T7LaDcPnblqxdIs/tNzvYfJHyfrXT0AFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFAFPUbIXlvtHDryprlWUqSrDBHb0rtqxtY0/cPtMXUffH9aAMGiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAp8UTTSLGgyx4pldJpOn/Zk82QfvW7f3RQBctLZbW3ES9up9TU9FFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAc7qum+SxnhH7s9V9Kyq7YgEEEZB7Vz2paUYCZYBmPuvpQBlUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRR+FbemaTgie4H+6tAC6TphGLibr/AAqa26P6UUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFAGNf6MH/e23Dd09fpWEylSVYYI657V21VLzT4bwZbhx0YUAcnRVu70+azOWG5P7wqpQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABTo4nmcIilmPartnpc11hj8kfqR1roLWzhs02xDnuT1NAFSw0lbfEk3zSemOBWnRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAhAIIIyD1HrWXdaJDNloT5bemMg1q0UAcjcWFxbH50O31HSq1dvVOfTLWfkx7W9V60AcpRWzNoDj/AFMob2YYqjLpt3F96Ikeq80AVKKVlKHDAg+9JQAUUUUAFFFFABRTkR5DhQSfYVai0q7l6RFf97igCnRW3DoHeab8FH9a0oNPtrc5SMZH8R5NAHPW2mXNzyF2r/eatq10eC3O5/3j+pHStGigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKAEZQwwRkVA1lauctAh+oqxRQBTOl2Z/wCWI/Kk/sqz/wCeVXaKAKa6ZZj/AJYKalWztk+7Ag+gqeigBAABgDFLRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUVBJeW8XDzKPbNVH1u0Tpub6CgDSorFbxAo+5bk/V8f0qFtfmP3YVH1OaAOgormzrt12CflSf25ef9M/++aAOlormxrt13VPyqVdflB+aFT9GxQBv0VjL4gQ/egK/Rs/0qxHrNo/dl+ooA0aKhjuoJf9XKrfQ1NQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRTZJEiTdIwUeprIuddVflt03H+8TxQBsEgDJOPqao3Gr2sHAbe3otc9cXc9ycyuT7dhUFAGtPrsznESCMe5yaz5ruefiSVmHoTUNFABRRRQAUUUUAFFFFABRRRQAUUUUAFWIb64g+5KQPTPFV6KANiHXpF4miDD1U4rSt9UtbjgPtY9mrlaKAO37d6Otcjb31xa/6tzj+6elbFrrkUuFnXy29c5BoA1qKRXV13KQQe9L3oAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKrXd9DZrukPzHoo6mgCwSAMk4FZN5rcceVtxvb1zxWVeajNeNg/Kn90VUoAlnuJbh90rlj71FiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAntrya1bMTED0PSt2z1iK4+WUeW/14Nc3RQB2+aK5ey1WW0+Rvnj9M9PpXRW91FdJvjbI7+ooAmooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKOtFYWpasWzDbnA/icd6ALOoautuTHDhpO57LXPSSPI5eQksepNNz3ooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACpIJ5LeQPGxDfzqOigDp7DU47wbW+SX0z1rQriQSpyDg+tb+m6sJcQznD/wt60Aa9FFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUZAGT09aKwNW1PzCbeE/L/E3r7UAN1TVPOzDCcR9z61k0UUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFAG5peq5xBOef4WNbdcRW/pOpeZi3mPz/AMLevtQBsUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUVS1K9FnBkcu3Cj+tAFXV9R8lfIiPzn7x9BXP0rMzsWY5J5JpKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKASCCDgiiigDp9Lv/tkex/8AWr1960K4uKV4ZVkQ4YGuss7pbuASKMHoV9DQBYooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKAGSyLFG0jnCr1rkru5a7uDK34D0rR1u83yC2Q/Kv3vc1j0AFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABVzTrw2dxk8o3DCqdFAHbAhhkdDS1jaJebkNs55XlfpWzQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAVWvroWls0nfoo9TVmua1m68668sH5Y+PxoAzmJZiSSSeppKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAfFK0MqyJ95TkV18Ey3EKyp0YZrja2dCutrtbt/Fyv1oA3qKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAr3tx9ltXk7jp9a5Akk5PPetjXbjdIkAPC5Y/jWPQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAU+KRopFkU/MDmmUUAdnDKs0SSL0YZqSsfQrjfE8DH7vIrYoAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACkYhVJPAHWlqjq03k2D46v8tAHNXExuLh5T1Y5qOiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAtadP9nvUb+EnDfSutriK6+ym+0WkcncjmgCxRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAVg6/LmSKIHoCT+Nb1cpqknm6hKf7p20AU+9FFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAV0Ggy7reSL+4Qfz/wD1Vz9aeiSbL0qejrQB0lFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFACE4BJri3Yu5Y9+a629fZZyt6Ka5CgAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACrFjJ5d7E3+0Kr0oOCD6UAdtRSKdygjuKWgAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigCjq7bdOk9+K5aiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigDsLNt9nE3qoqeiigAooooAKKKKACiiigAooooAKKKKACiiigD//2Q==");
                fileSystem.saveImage(String.valueOf(user.getId()), defaultImg, ImageType.USER);

                return true;
            }
        }
    }

    public User changePassword(Long userId, ChangePasswordDTO dto) {
        User user = userRepository.findUserById(userId).orElse(null);

        if(user == null) {
            return null;
        }
        else {
            if(passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
                user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
                return userRepository.save(user);
            }
        }
        return null;
    }

    @Transactional
    public Long deleteUserById(Long userId) {
        Long result = userRepository.deleteUserById(userId);

        return result;
    }
}