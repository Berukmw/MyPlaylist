package org.yearup.service;

import org.springframework.stereotype.Service;
import org.yearup.models.Profile;
import org.yearup.repository.ProfileRepository;

@Service
public class ProfileService
{
    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository)
    {
        this.profileRepository = profileRepository;
    }

    public Profile create(Profile profile)
    {
        return profileRepository.save(profile);
    }

    public Profile getByUserId(int userId)
    {
        return profileRepository.findById(userId).orElse(null);
    }

    public Profile update(int userId, Profile profile)
    {
        // profile's id is the userId itself, so this works the same as
        // the update pattern in CategoryService/ProductService -
        // grab the real row first, copy fields over, save it back
        Profile existing = profileRepository.findById(userId).orElseThrow();
        existing.setFirstName(profile.getFirstName());
        existing.setLastName(profile.getLastName());
        existing.setPhone(profile.getPhone());
        existing.setEmail(profile.getEmail());
        existing.setAddress(profile.getAddress());
        existing.setCity(profile.getCity());
        existing.setState(profile.getState());
        existing.setZip(profile.getZip());
        return profileRepository.save(existing);
    }
}