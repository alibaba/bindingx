Pod::Spec.new do |s|
  s.name             = 'Binding'
  s.version          = '0.0.1'
  s.summary          = 'Expression Binding.'

  s.description      = <<-DESC
  Expression Binding.
                       DESC

  s.homepage         = 'https://github.com/alibaba/binding'
  s.license          = { :type => 'MIT', :file => 'LICENSE' }
  s.author           = { 'hjhcn' => 'duixiang.hjh@alibaba-inc.com' }
  s.source           = { :git => 'https://github.com/alibaba/binding.git', :tag => s.version.to_s }

  s.ios.deployment_target = '7.0'

  s.source_files = [
    "ios/sdk/**/*.{h,m,mm}"
  ]
  s.dependency 'WeexSDK'
  s.dependency 'WeexPluginLoader'

end
