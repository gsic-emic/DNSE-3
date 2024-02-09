############################################
# Necesito recoger los ficheros a compilar #
############################################

# Incluye el listado de ficheros .cc
CC_SRCS := $(wildcard *.cc)

CC_DEPS := $(CC_SRCS:.cc=.d)

OBJS := $(CC_SRCS:.cc=.o)

# Librerías de ns-3 a enlazar
LIBS := -lrt -lm -ldl -lm -ldl -lpthread -lns3.18.1-antenna-debug -lns3.18.1-aodv-debug -lns3.18.1-applications-debug -lns3.18.1-bridge-debug -lns3.18.1-buildings-debug -lns3.18.1-config-store-debug -lns3.18.1-core-debug -lns3.18.1-csma-debug -lns3.18.1-csma-layout-debug -lns3.18.1-dsdv-debug -lns3.18.1-dsr-debug -lns3.18.1-emu-debug -lns3.18.1-energy-debug -lns3.18.1-fd-net-device-debug -lns3.18.1-flow-monitor-debug -lns3.18.1-internet-debug -lns3.18.1-lte-debug -lns3.18.1-mesh-debug -lns3.18.1-mobility-debug -lns3.18.1-mpi-debug -lns3.18.1-netanim-debug -lns3.18.1-network-debug -lns3.18.1-nix-vector-routing-debug -lns3.18.1-olsr-debug -lns3.18.1-point-to-point-debug -lns3.18.1-point-to-point-layout-debug -lns3.18.1-propagation-debug -lns3.18.1-spectrum-debug -lns3.18.1-stats-debug -lns3.18.1-tap-bridge-debug -lns3.18.1-test-debug -lns3.18.1-topology-read-debug -lns3.18.1-uan-debug -lns3.18.1-virtual-net-device-debug -lns3.18.1-wifi-debug -lns3.18.1-wimax-debug


# All Target
all: sim

# Tool invocations
sim: $(OBJS)
	@echo 'Building target: $@'
	@echo 'Invoking: GCC C++ Linker'
	g++ -L/usr/lib -L. -L/usr/local/ns-allinone-3.18.2/ns-3.18.1/build -o "sim" $(OBJS) $(USER_OBJS) $(LIBS)
	@echo 'Finished building target: $@'
	@echo ' '

# Other Targets
clean:
	-$(RM) $(CC_DEPS)$(C++_DEPS)$(EXECUTABLES)$(C_UPPER_DEPS)$(CXX_DEPS)$(OBJS)$(CPP_DEPS)$(C_DEPS) sim
	-@echo ' '

# Puede ser cc o cpp, de momento usaremos .cc
%.o: %.cc
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C++ Compiler'
	g++ -DNS3_LOG_ENABLE -I/usr/local/ns-allinone-3.18.2/ns-3.18.1/build -I. -I/usr/local/ns-allinone-3.18.2/ns-3.18.1/src/internet/model -O0 -g3 -Wall -c -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

print-%  : ; @echo $* = $($*)

.PHONY: all clean dependents
