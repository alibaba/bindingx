# coding: utf-8

Pod::Spec.new do |s|
  s.name         = "BindingX"
  s.version      = "1.0.0"
  s.summary      = "BindingX RN Plugin"

  s.description  = <<-DESC
                   It provides a way called expression binding for handling complex user interaction with views at 60 FPS in weex. 
                   DESC

  s.homepage     = "https://github.com/alibaba/bindingx"
  s.license = {
    :type => 'Copyright',
    :text => <<-LICENSE
           Alibaba-INC copyright
    LICENSE
  }
  s.authors      = {
                     "hjhcn" =>"380050803@qq.com"
                   }
  s.platform     = :ios
  s.ios.deployment_target = "7.0"

  s.source =  { :path => '.' }
  s.source_files  = "core/ios/BindingX/**/*.{h,m,mm}" , "react-native/lib/ios/RNBindingX/*.{h,m,mm}"
  
  s.requires_arc = true
  s.dependency "React"
  s.dependency "yoga"
end
