---
sidebar_position: 2
---

# Configure Rewards

Configure Rewards

---

## How to Configure Plugin Rewards

1. Open the `config.yml` file of RewardAdsInter. You will find a structure like this:

   ```yaml
   rewards:
     1234567:
       command:
         - "say %player% %name% %cost% %id%"
         - "ping"
     7654321:
       command:
         - "say HI"
   ```
2. Replace the numbers (1234567, 7654321, etc.) with your **reward ID**, which you can find in the RewardADs console.

3. Customize the commands according to your needs.

Each reward ID represents a different reward, and the commands listed under each ID will be executed when the reward is claimed.
