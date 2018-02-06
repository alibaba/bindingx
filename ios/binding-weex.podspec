Pod::Spec.new do |s|
  s.name             = 'BindingX'
  s.version          = '0.0.1'
  s.summary          = 'Expression Binding.'

  s.description      = <<-DESC
  Expression Binding.
                       DESC

  s.homepage         = 'https://github.com/alibaba/bindingx'
  s.license          = { :type => 'MIT', :file => 'LICENSE' }
  s.author           = { 'hjhcn' => '380050803@qq.com' }
  s.source           = { :git => 'https://github.com/alibaba/bindingx.git', :tag => s.version.to_s }

  s.ios.deployment_target = '7.0'

  s.source_files = [
    "sdk/Core/**/*.{h,m,mm}",
    "sdk/Weex/*.{h,m,mm}"
  ]
  s.dependency 'WeexSDK'
  s.dependency 'WeexPluginLoader'

end
