Pod::Spec.new do |s|

  s.name         = "TapstreamWOM"
  s.version      = "2.9.1"
  s.summary      = "Tapstream Word of Mouth"
  s.description  = <<-DESC
                   Tapstream Word of Mouth is a drop-in referral program for iOS. It rewards
                   your users for sharing your app with their friends.
                   DESC
  s.homepage     = "https://tapstream.com/word-of-mouth"

  s.license      = "MIT"
  s.author       = { "Benjamin Fox" => "support@tapstream.com" }

  s.platform     = :ios, "6.0"
  s.source       = { :git => "https://github.com/tapstream/tapstream-sdk.git", :tag => "v2.9.1-objc" }
  s.source_files = "objc/WordofMouth"
  s.dependency "Tapstream"

  s.requires_arc = true

end
